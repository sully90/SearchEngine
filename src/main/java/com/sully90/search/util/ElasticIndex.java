package com.sully90.search.util;

public enum ElasticIndex {

    MOVIES("movies");

    private String indexName;

    ElasticIndex(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }
}
