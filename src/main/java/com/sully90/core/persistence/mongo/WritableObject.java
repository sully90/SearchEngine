package com.sully90.core.persistence.mongo;

import com.sully90.core.persistence.mongo.util.FindableObject;
import com.sully90.core.persistence.mongo.util.ObjectWriter;

public interface WritableObject extends FindableObject {

    ObjectWriter writer();
}
