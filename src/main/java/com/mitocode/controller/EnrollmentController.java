package com.mitocode.controller;

import com.mitocode.model.Enrollment;
import com.mitocode.service.IEnrollmentService;
import com.mitocode.service.IEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
public class EnrollmentController {
    private final IEnrollmentService service;

    @GetMapping
    public Mono<ResponseEntity<List<Enrollment>>> findAll(){
        //public Mono<ResponseEntity<Flux<Enrollment>>> findAll(){
        /*Flux<Enrollment> fx = service.findAll();
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
    public Mono<ResponseEntity<Enrollment>> findById(@PathVariable("id") String id){
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Mono<ResponseEntity<Enrollment>> save(@Valid @RequestBody Enrollment enrollment, final ServerHttpRequest req){
        return service.save(enrollment)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Enrollment>> update(@Valid @PathVariable("id") String id, @RequestBody Enrollment enrollment){
        enrollment.setId(id);
        Mono<Enrollment> monoBody = Mono.just(enrollment);
        Mono<Enrollment> monoDB = service.findById(id);
        return monoDB.zipWith(monoBody, (db, enr)->{
                    db.setId(id);
                    db.setDateRegistration(enr.getDateRegistration());
                    db.setStudent(enr.getStudent());
                    db.setCourses(enr.getCourses());
                    db.setStatus(enr.getStatus());
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
    private Enrollment enrollmentHateoas;
    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel> getHateoas(@PathVariable("id") String id){
        Mono<Link> link1 = linkTo(methodOn(EnrollmentController.class).findById(id)).withSelfRel().toMono();
        return service.findById(id)
                .zipWith(link1, EntityModel::of);
    }
    @GetMapping("generateReport/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id){
        Mono<byte[]> monoReport = service.generateReport(id);

        return monoReport
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes)
                ).defaultIfEmpty(ResponseEntity.noContent().build());
    }

}
