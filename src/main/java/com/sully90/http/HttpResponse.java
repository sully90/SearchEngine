package com.sully90.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class HttpResponse {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static Response ok(Object entity) {
        return ok(Arrays.asList(entity));
    }

    public static Response ok(List<Object> entities) {
        try {
            return Response.status(HttpStatusCode.OK.getCode()).entity(objectMapper.writeValueAsString(entities)).build();
        } catch (JsonProcessingException e) {
            return Response.status(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode()).entity(e).build();
        }
    }

    public static Response internalServerError(Exception e) {
        return Response.status(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode()).entity(e).build();
    }

}
