package com.mitocode.repo;

import com.mitocode.model.Student;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface IStudentRepo extends IGenericRepo<Student, String> {
}
