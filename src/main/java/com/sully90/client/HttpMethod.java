package com.sully90.client;

public enum HttpMethod {

    GET("get"),
    POST("post"),
    PUT("put"),
    DELETE("delete");

    private String type;

    HttpMethod(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
