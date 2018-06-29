package com.qualys.meetup.webservice;

import com.qualys.meetup.entity.AddressUDT;
import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.exception.ServiceException;
import com.qualys.meetup.service.StudentService;
import com.qualys.meetup.utils.GenericRESTResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@Api(value = "Students information", description = "API's to save and fetch Students information", produces = "application/json", consumes = "application/json")
@Slf4j
public class StudentResource {

    @Autowired
    private StudentService studentService;

    @ApiOperation(value = "Get Student data", produces = "application/json", response = ResponseEntity.class,
            notes = "This API operation is used to get Student Data after applying given filter. ")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "name", value = "Search Student by its Name",
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(
                    name = "city", value = "Search Student by city",
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(
                    name = "email", value = "Search Student by its email",
                    dataType = "String", paramType = "email")
    })
    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity searchStudents(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "email", required = false) String email) {
        try {
            List<Map<String, Object>> students = studentService.searchStudent(name, city, email);
            MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            return GenericRESTResponseHandler.generateResponseJSON(students, header, false);
        } catch (ServiceException exception) {
            log.error(exception.getMessage());
            return GenericRESTResponseHandler.generateErrorResponseJSON(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return GenericRESTResponseHandler.generateErrorResponseJSON(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "API to save a student's information", produces = "application/json", response = ResponseEntity.class,
            notes = "This API operation is used to add student's information")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "name", value = "",
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(
                    name = "email", value = "",
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(
                    name = "street", value = "",
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(
                    name = "country", value = "",
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(
                    name = "pincode", value = "",
                    dataType = "Long", paramType = "query"),
            @ApiImplicitParam(
                    name = "city", value = "",
                    dataType = "String", paramType = "query")
    })
    @PutMapping(value = "/student", produces = {"application/json"})
    public ResponseEntity addStudents(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "street", required = false) String street,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "pincode", required = false) Long pincode,
            @RequestParam(name = "city", required = false) String city) {
        try {

            AddressUDT address = AddressUDT.of(street, city, country, pincode);
            StudentEntity studentEntity = StudentEntity.of(UUID.randomUUID(), name, address, email);
            studentService.addStudent(studentEntity);
            return GenericRESTResponseHandler.generateResponseJSON("Student details saved successfully", false);
        } catch (ServiceException exception) {
            log.error(exception.getMessage());
            return GenericRESTResponseHandler.generateErrorResponseJSON(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return GenericRESTResponseHandler.generateErrorResponseJSON(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
