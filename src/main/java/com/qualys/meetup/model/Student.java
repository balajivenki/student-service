package com.qualys.meetup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qualys.meetup.entity.StudentEntity;
import lombok.Data;

import java.util.UUID;

/**
 * Created by aagarwal on 6/12/2018.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {

    @JsonProperty
    private UUID studentId;

    @JsonProperty
    private Address address;

    @JsonProperty
    private String name;

    @JsonProperty
    private String email;

    @JsonIgnore
    public static Student fromEntityToModel(StudentEntity studentEntity) {
        Student student = new Student();
        student.setStudentId(studentEntity.getStudentId());
        student.setAddress(Address.fromEntityToModel(studentEntity.getAddress()));
        student.setName(studentEntity.getName());
        student.setEmail(studentEntity.getEmail());
        return student;
    }
}
