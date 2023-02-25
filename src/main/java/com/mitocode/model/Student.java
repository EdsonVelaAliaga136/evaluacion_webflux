package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "students")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    @Size(min = 3, max = 100)
    @Field
    private String name;
    @Field
    @Size(min = 3, max = 100)
    private String lastname;
    @Field
    @Size(min = 7)
    private String  dni;
    @Field
    private Integer age;
}
