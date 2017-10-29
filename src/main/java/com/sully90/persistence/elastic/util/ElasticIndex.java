package com.sully90.persistence.elastic.util;

import com.sully90.elasticutils.persistence.elastic.util.ElasticIndexNames;

public enum ElasticIndex implements ElasticIndexNames {

    MOVIES("movies");

    private String indexName;

    ElasticIndex(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public String getIndexName() {
        return this.indexName;
    }
}
