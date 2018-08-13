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
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.qualys.meetup"})
public class StudentServiceApplication {

	public static void main(String[] args) {
		try {
			String consulHost = System.getProperty("spring.cloud.consul.host");
			log.info("consulHost System Property value is -- " + consulHost);
			String consulPort = System.getProperty("spring.cloud.consul.port");
			log.info("consulPort System Property value is -- " + consulPort);
			String consulBaseURL = "http://" + consulHost + ":" + consulPort;
			log.info("consulBaseURL System Property value is -- " + consulBaseURL);
			String consulPrefix = System.getProperty("spring.cloud.consul.config.prefix");
			log.info("consulPrefix System Property value is -- " + consulPrefix);
			SpringApplication.run(StudentServiceApplication.class, args);
			log.info("Student Service Started Successfully on " + new Date());
		} catch (Exception e) {
			log.error("Failed to start Student Service", e);
		}
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
