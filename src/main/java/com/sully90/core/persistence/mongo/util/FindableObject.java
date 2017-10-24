package com.sully90.core.persistence.mongo.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;

public interface FindableObject {

    @JsonIgnore
    ObjectId getObjectId();
}
