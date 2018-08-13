package com.qualys.meetup.service;

import com.google.common.collect.Maps;
import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.model.Student;
import com.qualys.meetup.utils.ServiceConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by aagarwal on 6/12/2018.
 */
@Service
public class StudentService {

    @Autowired
    private ElasticService elasticService;

    @Autowired
    private CassandraService cassandraService;

    public List<Map<String, Object>> searchStudent(String name, String city, String email) {
        Map<String, String> queryMap = Maps.newHashMapWithExpectedSize(3);

        if (StringUtils.isNotEmpty(name))
            queryMap.put(ServiceConstant.NAME, name);
        if (StringUtils.isNotEmpty(city))
            queryMap.put(ServiceConstant.CITY, city);
        if (StringUtils.isNotEmpty(email))
            queryMap.put(ServiceConstant.EMAIL, email);

        if (queryMap.size() > 0)
            return elasticService.search(queryMap, ServiceConstant.STUDENT_TYPE);
        else
            return Collections.emptyList();

    }

    public String addStudent(StudentEntity studentEntity) {
        cassandraService.saveStudent(studentEntity);
        Student student = Student.fromEntityToModel(studentEntity);
        elasticService.index(student, ServiceConstant.STUDENT_INDEX, ServiceConstant.STUDENT_TYPE, student.getStudentId().toString());
        return "Student information saved successfully";
    }
}
