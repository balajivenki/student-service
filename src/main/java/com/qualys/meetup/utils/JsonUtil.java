package com.qualys.meetup.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.qualys.meetup.exception.ServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


public class JsonUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert to Json  String, " + e.getMessage());
        }
    }

    public static LinkedHashMap convertToMap(String jsonString) {

        try {
            return objectMapper.readValue(jsonString, LinkedHashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert to Json  String, " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert to Json  String, " + e.getMessage());
        }
    }

    public static List convertToList(String jsonString) {

        try {
            return objectMapper.readValue(jsonString, ArrayList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert to List, " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert to List, " + e.getMessage());
        }
    }

    /**
     *
     * @param incomingResponse
     * @param attributesToFetch
     * @return
     * accepts object containing all fields and attributes list to filter
     * and returns only required fields filtering out the rest
     */
    public static Object filterJsonResponse(Object incomingResponse, Set<String> attributesToFetch) throws ServiceException {
        Object filteredJson = null;
        String filterId = null;
        if(incomingResponse == null)
        {
            return null;
        }
        if (incomingResponse instanceof List && !((List)incomingResponse).isEmpty()) {
            filterId = ((List) incomingResponse).get(0).getClass().getName();
        } else {
            filterId = incomingResponse.getClass().getName();
        }
        // setFailOnUnknownId is set to false to avoid exception in case of fetching nested objects, the complete object is being returned now
        FilterProvider filters = new SimpleFilterProvider().addFilter(filterId, SimpleBeanPropertyFilter
                .filterOutAllExcept(attributesToFetch)).setFailOnUnknownId(false);
        try {
            filteredJson = objectMapper.setAnnotationIntrospector(new CVFilteringIntrospector()).writer(filters).withDefaultPrettyPrinter().writeValueAsString(incomingResponse);
        } catch (JsonParseException jex) {
            throw new ServiceException("Json response filter error",jex);
        } catch (Exception ex) {
            throw new ServiceException("Json response filter error",ex);
        }
        return filteredJson;
    }

    private static class CVFilteringIntrospector extends
            JacksonAnnotationIntrospector {
        @Override
        public Object findFilterId(Annotated a) {
            Object id = super.findFilterId(a);
            if (id == null) {
                //returning id as the fully qualified name of the class
                id = a.getName();
            }
            return id;
        }
    }
}
