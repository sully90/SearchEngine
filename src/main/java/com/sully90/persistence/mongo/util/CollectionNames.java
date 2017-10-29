package com.sully90.persistence.mongo.util;

import com.sully90.elasticutils.persistence.mongo.util.MongoCollectionNames;

public enum CollectionNames implements MongoCollectionNames {
    MOVIES("movies");

    private String name;

    CollectionNames(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
