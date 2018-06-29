package com.qualys.meetup.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class GenericRESTResponseHandler {
    public static ResponseEntity generateResponseJSON(Object incomingObject){
        Map<String, Object> responseJSON = new HashMap<String , Object>();
        String objectClassName = incomingObject.getClass().getSimpleName();
        if(incomingObject instanceof List && !((List)incomingObject).isEmpty()){
            objectClassName+="(s)";
        }

        responseJSON.put(objectClassName, incomingObject);
        return new ResponseEntity(responseJSON,HttpStatus.OK);
    }

    public static ResponseEntity generateResponseJSON(Object incomingObject, boolean showClassName){
        Map<String, Object> responseJSON = new HashMap<String , Object>();
        String objectClassName = incomingObject.getClass().getSimpleName();
        if(incomingObject instanceof List && !((List)incomingObject).isEmpty()){
            objectClassName+="(s)";
        }
        if(showClassName){
            responseJSON.put(objectClassName, incomingObject);
            return new ResponseEntity(responseJSON,HttpStatus.OK);
        }else{
            return new ResponseEntity(incomingObject, HttpStatus.OK);
        }
    }

    public static ResponseEntity generateResponseJSON(Object incomingObject, MultiValueMap<String, String> headers, boolean showClassName){
        Map<String, Object> responseJSON = new HashMap<String , Object>();
        String objectClassName = incomingObject.getClass().getSimpleName();
        if(incomingObject instanceof List && !((List)incomingObject).isEmpty()){
            objectClassName+="(s)";
        }
        if(showClassName){
            responseJSON.put(objectClassName, incomingObject);
            return new ResponseEntity(responseJSON, headers, HttpStatus.OK);
        }else{
            return new ResponseEntity(incomingObject, headers, HttpStatus.OK);
        }
    }

    public static ResponseEntity generateResponseJSON(Object incomingObject, MultiValueMap<String, String> headers, String attributesToFetch) {
        if (attributesToFetch != null) {
            Set<String> attributes = new HashSet<>(Arrays.asList(attributesToFetch.split(",")));
            return new ResponseEntity(JsonUtil.filterJsonResponse(incomingObject, attributes), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity(incomingObject, headers, HttpStatus.OK);
        }
    }

    public static ResponseEntity generateErrorResponseJSON(String errorMessage, HttpStatus statusCode){
        Map<String, Object> errorResponseJSON = new HashMap<String , Object>();
        Map<String, Object> errorBlockJSON = new HashMap<String , Object>();
        errorBlockJSON.put("code", statusCode.value());
        errorBlockJSON.put("message", errorMessage);
        errorResponseJSON.put("_error", errorBlockJSON);
        return new ResponseEntity(errorResponseJSON,statusCode);
    }
}
