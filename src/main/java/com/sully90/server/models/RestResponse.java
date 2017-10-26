package com.sully90.server.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sully90.http.HttpStatusCode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "entities"
})
public class RestResponse<T> implements Serializable {

    @JsonIgnore
    public static RestResponse build() {
        return new RestResponse<>();
    }

    private String status;

    @JsonProperty("entities")
    private List<T> entities;

    public RestResponse() {

    }

    @JsonIgnore
    public RestResponse status(HttpStatusCode status) {
        this.status = String.valueOf(status.getCode());
        return this;
    }

    @JsonIgnore
    public RestResponse entity(T entity) {
        return this.entities(Arrays.asList(entity));
    }

    @JsonIgnore
    public RestResponse entities(List<T> entities) {
        this.entities = entities;
        return this;
    }

    public RestResponse(HttpStatusCode status) {
        this.status = String.valueOf(status.getCode());
    }

    public RestResponse(HttpStatusCode status, List<T> entities){
        this.status = String.valueOf(status.getCode());
        this.entities = entities;
    }

    public String getStatus() {
        return this.status;
    }

    @JsonProperty("entities")
    public List<T> getEntities() {
        return entities;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }
}
