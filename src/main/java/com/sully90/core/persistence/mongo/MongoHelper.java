package com.sully90.core.persistence.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sully90.core.persistence.mongo.util.DatabaseConnection;
import com.sully90.core.persistence.mongo.util.DatabaseType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MongoHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoHelper.class);

    private static Map<DatabaseType, DatabaseConnection> connectionMap;

    static {
        connectionMap = new ConcurrentHashMap<DatabaseType, DatabaseConnection>();

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

        for (DatabaseType databaseType : DatabaseType.values()) {
            if(LOGGER.isInfoEnabled()) LOGGER.info(String.format("Attempting to make connection to db %s", databaseType.getLabel()));
            DB db = mongoClient.getDB(databaseType.getLabel());
            if(LOGGER.isInfoEnabled()) LOGGER.info(String.format("Successfully made connection to db %s", databaseType.getLabel()));

            Jongo jongo = new Jongo(db);

            DatabaseConnection databaseConnection = new DatabaseConnection(jongo);

            connectionMap.put(databaseType, databaseConnection);
        }
    }

    public static DatabaseConnection getDatabase(DatabaseType databaseType) {
        return connectionMap.get(databaseType);
    }

}
