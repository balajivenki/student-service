package com.qualys.meetup.config;

import info.archinnov.achilles.generated.manager.StudentEntity_Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import info.archinnov.achilles.generated.ManagerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {

    @Autowired
    protected ManagerFactory managerFactory;

    @Bean
    StudentEntity_Manager studentEntityManager(){
       return managerFactory.forStudentEntity();
    }
}
