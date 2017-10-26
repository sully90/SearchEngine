package com.sully90.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sully90.server.models.RestResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HttpResponse {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static RestResponse ok(Object entity) {
        if (entity instanceof Collection) {
            return ok(new ArrayList<>((Collection) entity));
        } else {
            return ok(Arrays.asList(entity));
        }
    }

    public static RestResponse ok(List<Object> entities) {
        return RestResponse.build().status(HttpStatusCode.OK).entities(entities);
    }

    public static RestResponse internalServerError() {
        return RestResponse.build().status(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    public static RestResponse internalServerError(Exception e) {
        return RestResponse.build().status(HttpStatusCode.INTERNAL_SERVER_ERROR).entity(e);
    }

}
