package com.mitocode.handler;

import com.mitocode.model.Student;
import com.mitocode.service.IStudentService;
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
public class StudentHandler {

    private final IStudentService service;
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
                .body(service.findAll(), Student.class);
    }
    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");
        return  service.findById(id)
                .flatMap(student -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(student))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Student> monoStudent = req.bodyToMono(Student.class);

        return monoStudent
                .flatMap(requestValidator::validate)
                .flatMap(service::save)
                .flatMap(student -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(student.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(student))
                );
    }
    public Mono<ServerResponse> update(ServerRequest req){
        String id = req.pathVariable("id");
        Mono<Student> monoStudent = req.bodyToMono(Student.class);
        Mono<Student> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoStudent, (db, st) ->{
                    db.setId(id);
                    db.setName(st.getName());
                    db.setLastname(st.getLastname());
                    db.setDni(st.getDni());
                    db.setAge(st.getAge());
                    return db;
                })
                .flatMap(requestValidator::validate)
                .flatMap(service::update)
                .flatMap(student -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(student))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(student -> service.delete(student.getId()))
                .then(ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    public Mono<ServerResponse> findAllByAgeASC(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findByAgeASC(), Student.class);
    }
    public Mono<ServerResponse> findAllByAgeDESC(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findByAgeDESC(), Student.class);
    }
}
