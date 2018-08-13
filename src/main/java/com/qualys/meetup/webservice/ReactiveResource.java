package com.qualys.meetup.webservice;

import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.service.CassandraService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Created by aagarwal on 6/28/2018.
 */
@Slf4j
@RestController("/reactive")
@Api(value = "Reactive Student's APIs",
		description = "Reactive API's to fetch Student's information",
		produces = "application/json", consumes = "application/json")
public class ReactiveResource {

	@Autowired
	private CassandraService cassandraService;


	@ApiOperation(value = "Search Student By ID", produces = "application/json", response = ResponseEntity.class,
			notes = "This API operation is used to search student's information")
	@ApiImplicitParams({
			@ApiImplicitParam(
					name = "id", value = "",
					dataType = "String", paramType = "path")
	})
	@GetMapping("/students/{id}")
	public Mono<ResponseEntity> searchStudentById(@PathVariable(name = "id") String studentId) {
		UUID studentUUID = UUID.fromString(studentId);
		Mono<StudentEntity> studentEntityMono = cassandraService.getStudentById(studentUUID);
		return studentEntityMono.map(event -> new ResponseEntity(event, HttpStatus.OK)).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
