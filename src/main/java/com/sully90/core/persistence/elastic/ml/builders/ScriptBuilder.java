package com.sully90.core.persistence.elastic.ml.builders;

import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;

public interface ScriptBuilder {

    ScriptScoreFunctionBuilder getScript();

}
