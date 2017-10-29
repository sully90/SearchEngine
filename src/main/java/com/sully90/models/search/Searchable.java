package com.sully90.models.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sully90.elasticutils.persistence.mongo.WritableObject;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public abstract class Searchable implements WritableObject {

    private String mongoId;

    // Hashmap to store results from the OpenNLP plugin
    private Map<String, String[]> entities;

    protected Searchable() {
        this.entities = new HashMap<>();
    }

    @JsonIgnore
    public abstract ObjectId getObjectId();

    public String getMongoId() {
        if (this.mongoId == null) {
            this.setMongoId(this.getObjectId().toString());
        }
        return this.mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public Map<String, String[]> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, String[]> entities) {
        this.entities = entities;
    }
}
