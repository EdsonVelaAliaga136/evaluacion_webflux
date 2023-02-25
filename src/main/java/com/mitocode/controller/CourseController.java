package com.mitocode.controller;


import com.mitocode.model.Course;
import com.mitocode.service.ICourseService;
import com.mitocode.service.ICourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final ICourseService service;

    @GetMapping
    public Mono<ResponseEntity<List<Course>>> findAll(){
        /*Flux<Course> fx = service.findAll();
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
    public Mono<ResponseEntity<Course>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Mono<ResponseEntity<Course>> save(@Valid @RequestBody Course course, final ServerHttpRequest req){
        return service.save(course)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Course>> update(@Valid @PathVariable("id") String id, @RequestBody Course course){
        course.setId(id);
        Mono<Course> monoBody = Mono.just(course);
        Mono<Course> monoDB = service.findById(id);
        return monoDB.zipWith(monoBody, (db, cou)->{
                    db.setId(id);
                    db.setName(cou.getName());
                    db.setAcronym(cou.getAcronym());
                    db.setStatus(cou.getStatus());
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
    private Course courseHateoas;
    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel> getHateoas(@PathVariable("id") String id){
        Mono<Link> link1 = linkTo(methodOn(CourseController.class).findById(id)).withSelfRel().toMono();
        return service.findById(id)
                .zipWith(link1, EntityModel::of);
    }
}
