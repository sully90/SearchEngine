package com.sully90.server.models;

import com.sully90.http.HttpStatusCode;

import java.util.Arrays;
import java.util.List;

public class RestResponse<T> {

    public static RestResponse build() {
        return new RestResponse<>();
    }

    private HttpStatusCode status;
    private List<T> entities;

    public RestResponse() {

    }

    public RestResponse status(HttpStatusCode status) {
        this.status = status;
        return this;
    }

    public RestResponse entity(T entity) {
        return this.entity(Arrays.asList(entity));
    }

    public RestResponse entity(List<T> entities) {
        this.entities = entities;
        return this;
    }

    public RestResponse(HttpStatusCode status) {
        this.status = status;
    }

    public RestResponse(HttpStatusCode status, List<T> entities){
        this.status = status;
        this.entities = entities;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public List<T> getEntities() {
        return entities;
    }
}
