package com.sully90.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sully90.server.models.RestResponse;

import java.util.Arrays;

public class HttpResponse {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static RestResponse ok(Object entity) {
        return ok(Arrays.asList(entity));
    }

    public static RestResponse ok(Iterable<Object> entities) {
        try {
            return RestResponse.build().status(HttpStatusCode.OK).entity(objectMapper.writeValueAsString(entities));
        } catch (JsonProcessingException e) {
            return internalServerError(e);
        }
    }

    public static RestResponse internalServerError() {
        return RestResponse.build().status(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    public static RestResponse internalServerError(Exception e) {
        return RestResponse.build().status(HttpStatusCode.INTERNAL_SERVER_ERROR).entity(e);
    }

}
