package com.qualys.meetup.service;

import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.exception.ServiceException;
import info.archinnov.achilles.generated.manager.StudentEntity_Manager;
import info.archinnov.achilles.type.strategy.InsertStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by aagarwal on 6/27/2018.
 */
@Service
public class CassandraService {

    @Autowired
    private StudentEntity_Manager studentEntityManager;

    public void saveStudent(StudentEntity studentEntity){
        try {
            studentEntityManager.crud()
                    .insert(studentEntity)
                    .withInsertStrategy(InsertStrategy.NOT_NULL_FIELDS)
                    .execute();
        } catch (Exception e) {
            throw new ServiceException("Unable to Save", e);
        }
    }

}
