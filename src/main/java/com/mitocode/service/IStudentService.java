package com.mitocode.service;

import com.mitocode.model.Student;
import com.mitocode.repo.IStudentRepo;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IStudentService extends ICRUD<Student, String> {
    Flux<Student> findByAgeASC();
    Flux<Student> findByAgeDESC();
}
