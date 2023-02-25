package com.mitocode.handler;

import com.mitocode.model.Enrollment;
import com.mitocode.service.IEnrollmentService;
import com.mitocode.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class EnrollmentHandler {

    private final IEnrollmentService service;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> findAll(ServerRequest req){

        /*return service.findAll()
                .hasElements()
                .flatMap(status -> {
                    if (status)
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(service.findAll()));
                        else
                            return ServerResponse.noContent().build();
                });*/

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Enrollment.class);
    }
    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return  service.findById(id)
                .flatMap(enrollment -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(enrollment))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Enrollment> monoEnrollment = req.bodyToMono(Enrollment.class);

        return monoEnrollment
                .flatMap(requestValidator::validate)
                .flatMap(service::save)
                .flatMap(enrollment -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(enrollment.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(enrollment))
                );
    }
    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");
        Mono<Enrollment> monoEnrollment = req.bodyToMono(Enrollment.class);
        Mono<Enrollment> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoEnrollment, (db, enr) ->{
                    db.setId(id);
                    db.setDateRegistration(enr.getDateRegistration());
                    db.setStudent(enr.getStudent());
                    db.setCourses(enr.getCourses());
                    db.setStatus(enr.getStatus());
                    return db;
                })
                .flatMap(requestValidator::validate)
                .flatMap(service::update)
                .flatMap(enrollment -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(enrollment))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(enrollment -> service.delete(enrollment.getId()))
                .then(ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
