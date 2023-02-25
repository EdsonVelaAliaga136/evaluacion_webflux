package com.mitocode.repo;

import com.mitocode.model.Course;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ICourseRepo extends IGenericRepo<Course, String> {
}
