package com.mitocode.service.impl;

import com.mitocode.model.Student;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IStudentRepo;
import com.mitocode.service.IStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends CRUDImpl<Student, String> implements IStudentService {

    private final IStudentRepo repo;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    protected IGenericRepo<Student, String> getRepo() {
        return repo;
    }

    public Flux<Student> findByAgeASC() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "age"));
    }
    public Flux<Student> findByAgeDESC() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "age"));
    }
}
