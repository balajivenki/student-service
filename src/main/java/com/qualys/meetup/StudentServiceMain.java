package com.qualys.meetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import java.util.Date;

/**
 * Created by aagarwal on 6/27/2018.
 */
@SpringBootApplication(scanBasePackages = {"com.qualys.meetup"})
@EnableDiscoveryClient
@Slf4j
public class StudentServiceMain {

    public static void main(String[] args) {

        String consulHost = System.getProperty("spring.cloud.consul.host");
        log.info("consulHost System Property value is -- " + consulHost);
        String consulPort = System.getProperty("spring.cloud.consul.port");
        log.info("consulPort System Property value is -- " + consulPort);
        String consulBaseURL = "http://" + consulHost + ":" + consulPort;
        log.info("consulBaseURL System Property value is -- " + consulBaseURL);
        String consulPrefix = System.getProperty("spring.cloud.consul.config.prefix");
        log.info("consulPrefix System Property value is -- " + consulPrefix);
        SpringApplication.run(StudentServiceMain.class, args);
        log.info("Student Service Started Successfully on "+ new Date());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
