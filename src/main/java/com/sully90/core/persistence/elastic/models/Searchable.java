package com.sully90.core.persistence.elastic.models;

import java.util.Map;

public abstract class Searchable {

    private Map<String, String[]> entities;

    public Map<String, String[]> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, String[]> entities) {
        this.entities = entities;
    }
}
