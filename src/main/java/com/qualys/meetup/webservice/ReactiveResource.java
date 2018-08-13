package com.qualys.meetup.webservice;

import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.service.CassandraService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by aagarwal on 6/28/2018.
 */
@Slf4j
@RestController("/reactive")
@Api(value = "Reactive Student's APIs",
		description = "Reactive APIs to fetch Student's information",
		produces = "application/json", consumes = "application/json")
public class ReactiveResource {

	@Autowired
	private CassandraService cassandraService;

	@ApiOperation(value = "List all Student's Data from Database", produces = "application/json",
			response = Flux.class,
			notes = "This API operation is used for listing all student's information")
	@GetMapping("/all-students")
	public Flux<ResponseEntity> getAllStudents() {
		Flux<StudentEntity> studentEntityFlux = cassandraService.getAllStudents();
		return studentEntityFlux.map(student -> new ResponseEntity(student, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@ApiOperation(value = "List multiple Students by Ids", produces = "application/json",
			response = Mono.class,
			notes = "This API operation is used for listing multiple student's information by Ids")
	@ApiImplicitParams({
			@ApiImplicitParam(
					name = "studentIds", value = "", dataType = "String", paramType = "query")
	})
	@GetMapping("/multiple-students")
	public Mono<List<StudentEntity>> getMultipleStudents(@RequestParam(name = "studentIds", required = true) String studentIds) {
		if (!StringUtils.hasText(studentIds)) return Mono.empty();
		List<String> studentIdStrList = Arrays.asList(studentIds.split("\\s*,\\s*"));
		if (CollectionUtils.isNotEmpty(studentIdStrList)) {
			List<UUID> uuidList = new ArrayList<>();
			studentIdStrList.forEach(s -> uuidList.add(UUID.fromString(s)));
			return cassandraService.getStudentById(uuidList);
		}
		return Mono.empty();
	}


	@ApiOperation(value = "Search Student By ID", produces = "application/json",
			response = Mono.class,
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
		return studentEntityMono.map(student -> new ResponseEntity(student, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
