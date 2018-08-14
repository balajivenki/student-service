package com.qualys.meetup.entity;

import info.archinnov.achilles.annotations.*;
import lombok.Data;
import java.util.UUID;

/**
 * Created by aagarwal on 6/12/2018.
 */
@Table(keyspace = "meetup", table = "student")
@Data
public class StudentEntity {

    @Column("student_id")
    @PartitionKey(value = 1)
    private UUID studentId;

    @Column("name")
    @ClusteringColumn(value = 1)
    private String name;

    @Column("address")
    @Frozen
    private AddressUDT address;

    @Column("email")
    private String email;

    public static StudentEntity of(UUID studentId, String name, AddressUDT addressUDT, String email){
        StudentEntity student = new StudentEntity();
        student.setStudentId(studentId);
        student.setName(name);
        student.setAddress(addressUDT);
        student.setEmail(email);
        return student;
    }
}
