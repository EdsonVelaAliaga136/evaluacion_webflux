package com.mitocode.config;

import com.mitocode.handler.CourseHandler;
import com.mitocode.handler.EnrollmentHandler;
import com.mitocode.handler.StudentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

@Configuration
public class RouterConfig {
    // Functional Endpoints

    @Bean
    public RouterFunction<ServerResponse> routesStudents(StudentHandler handler){
        return route(GET("/v2/students"), handler::findAll)
                .andRoute(GET("v2/students/{id}"), handler::findById)
                .andRoute(POST("v2/students"), handler::create)
                .andRoute(PUT("v2/students/{id}"), handler::update)
                .andRoute(DELETE("v2/students/{id}"), handler::delete)
                .andRoute(GET("v2/asc"), handler::findAllByAgeASC)
                .andRoute(GET("v2/desc"), handler::findAllByAgeDESC);
    }
    @Bean
    public RouterFunction<ServerResponse> routesCourses(CourseHandler handler){
        return route(GET("/v2/courses"), handler::findAll)
                .andRoute(GET("v2/courses/{id}"), handler::findById)
                .andRoute(POST("v2/courses"), handler::create)
                .andRoute(PUT("v2/courses/{id}"), handler::update)
                .andRoute(DELETE("v2/courses/{id}"), handler::delete);
    }
    @Bean
    public RouterFunction<ServerResponse> routesEnrollments(EnrollmentHandler handler){
        return route(GET("/v2/enrollments"), handler::findAll)
                .andRoute(GET("v2/enrollments/{id}"), handler::findById)
                .andRoute(POST("v2/enrollments"), handler::create)
                .andRoute(PUT("v2/enrollments/{id}"), handler::update)
                .andRoute(DELETE("v2/enrollments/{id}"), handler::delete);
    }

}
