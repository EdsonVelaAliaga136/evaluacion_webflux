package com.mitocode.service;

import com.mitocode.model.Course;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICourseService extends ICRUD<Course, String> {
}
