package com.sully90.core.persistence.elastic.ml;

import org.bson.types.ObjectId;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import com.sully90.core.persistence.elastic.ml.builders.ScoreScriptBuilder;
import com.sully90.core.persistence.mongo.WritableObject;
import com.sully90.core.persistence.mongo.util.CollectionNames;
import com.sully90.core.persistence.mongo.util.ObjectFinder;
import com.sully90.core.persistence.mongo.util.ObjectWriter;

public class ScoreScript<T> implements WritableObject {

    private ObjectId _id;
    private Class<T> returnClass;

    private ScoreScriptBuilder<T> builder;
    private Script script;

    public ScoreScript(Class<T> returnClass) {
        this.returnClass = returnClass;
        this.builder = new ScoreScriptBuilder<>(this.returnClass);
    }

    private ScoreScript() {
        // For Jackson
    }

    public ScoreScriptBuilder builder() {
        return builder;
    }

    public ScriptScoreFunctionBuilder getScript() {
        return this.builder().getScript();
    }

    @Override
    public ObjectWriter writer() {
        return new ObjectWriter(CollectionNames.SCORE_SCRIPTS, this);
    }

    @Override
    public ObjectId getObjectId() {
        return this._id;
    }

    public static ObjectFinder<ScoreScript> finder() {
        return new ObjectFinder<ScoreScript>(CollectionNames.SCORE_SCRIPTS, ScoreScript.class);
    }

    public Class<T> getReturnClass() {
        return returnClass;
    }
}
