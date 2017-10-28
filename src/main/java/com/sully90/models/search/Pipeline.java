package com.sully90.models.search;

public enum Pipeline {
    OPENNLP("opennlp-pipeline");

    private String pipelineName;

    Pipeline(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }
}
