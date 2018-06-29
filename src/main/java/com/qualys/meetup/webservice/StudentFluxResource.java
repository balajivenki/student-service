package com.qualys.meetup.webservice;

import com.qualys.meetup.entity.AddressUDT;
import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.service.StudentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Created by aagarwal on 6/28/2018.
 */
@Configuration
public class StudentFluxResource {

    @Bean
    RouterFunction<?> routes(StudentService studentService) {
        return RouterFunctions.route(RequestPredicates.GET("/reactive/students"),
                r -> ServerResponse.ok().body(Flux.just(studentService.searchStudent(r.queryParam("name").get(),r.queryParam("email").get(),r.queryParam("city").get())), List.class))
                .andRoute(RequestPredicates.PUT("/reactive/student"),
                        r -> ServerResponse.ok().body(Flux.just(
                                studentService.addStudent(StudentEntity.of(UUID.randomUUID(),r.queryParam("name").get(),
                                        AddressUDT.of(r.queryParam("street").get(),
                                                r.queryParam("city").get(),
                                                r.queryParam("country").get(),
                                                Long.valueOf(r.queryParam("pincode").get()))
                                        ,r.queryParam("email").get()
                                        ))
                        ).delayElements(
                                Duration.ofSeconds(5000)), String.class));

    }

}
