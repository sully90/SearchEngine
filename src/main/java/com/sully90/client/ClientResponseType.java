package com.sully90.client;

public enum ClientResponseType {

    JSON("application/json");

    private String type;

    ClientResponseType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
