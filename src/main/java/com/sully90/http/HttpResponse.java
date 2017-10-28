package com.sully90.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HttpResponse {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static Response ok(Object entity) {
        if (entity instanceof Collection) {
            return ok(new ArrayList<>((Collection) entity));
        } else {
            return ok(Arrays.asList(entity));
        }
    }

    public static Response ok(List<Object> entities) {
        return Response.status(HttpStatusCode.OK.getCode()).entity(entities).build();
    }

    public static Response created(Object entity) {
        return Response.status(HttpStatusCode.CREATED.getCode()).entity(entity).build();
    }

    public static Response internalServerError() {
        return Response.status(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
    }

    public static Response internalServerError(Exception e) {
        return Response.status(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode()).entity(e).build();
    }

}
