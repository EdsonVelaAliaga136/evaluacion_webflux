package com.mitocode.controller;


import com.mitocode.model.Student;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
// import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final IStudentService service;

    @GetMapping
    public Mono<ResponseEntity<List<Student>>> findAll(){
    //public Mono<ResponseEntity<Flux<Student>>> findAll(){
        /*Flux<Student> fx = service.findAll();
        return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fx))
                .defaultIfEmpty(ResponseEntity.noContent().build());*/

        return service.findAll()
                .collectList()
                .map(list -> {
                    if(list.isEmpty())
                        return ResponseEntity.noContent().build();
                    else
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(list);
                });
    }
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Student>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Mono<ResponseEntity<Student>> save(@Valid @RequestBody Student student, final ServerHttpRequest req){
       return service.save(student)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Student>> update(@Valid @PathVariable("id") String id, @RequestBody Student student){
        student.setId(id);
        Mono<Student> monoBody = Mono.just(student);
        Mono<Student> monoDB = service.findById(id);
        return monoDB.zipWith(monoBody, (db, stu)->{
            db.setId(id);
            db.setName(stu.getName());
            db.setLastname(stu.getLastname());
            db.setDni(stu.getDni());
            db.setAge(stu.getAge());
            return db;
        })
                .flatMap(service::update)
                .map(e-> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                   .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){
        return service.findById(id)
                .flatMap(e-> service.delete(e.getId())
                        .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    private Student studentHateoas;
    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel> getHateoas(@PathVariable("id") String id){
        Mono<Link> link1 = linkTo(methodOn(StudentController.class).findById(id)).withSelfRel().toMono();
        return service.findById(id)
                .zipWith(link1, EntityModel::of);
    }
    // Pageable
    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Student>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size
    ){
        return service.getPage(PageRequest.of(page, size))
                .map(pag -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pag)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    // Listar estudiantes ordenados de forma ascendente por edad
    @GetMapping("/asc")
    public Mono<ResponseEntity<List<Student>>> findAllAgeASC(){
        return service.findByAgeASC()
                .collectList()
                .map(list -> {
                    if(list.isEmpty())
                        return ResponseEntity.noContent().build();
                    else
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(list);
                });
    }
    //Listar estudiantes ordenados de forma descendente por edad
    @GetMapping("/desc")
    public Mono<ResponseEntity<List<Student>>> findAllAgeDESC(){
        return service.findByAgeDESC()
                .collectList()
                .map(list -> {
                    if(list.isEmpty())
                        return ResponseEntity.noContent().build();
                    else
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(list);
                });
    }
}
