package com.sully90.server.models;

public class UpdateRequest {

    private String mongoId;
    private Integer originalRank;
    private Integer judgement;  // a score between 0 and 3, where 0 is an irrelevant result and 3 is a perfect score

    public UpdateRequest(String mongoId, Integer originalRank, Integer judgement) {
        this.mongoId = mongoId;
        this.originalRank = originalRank;
        this.judgement = judgement;
    }

    private UpdateRequest() {
        // For Jackson
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public Integer getOriginalRank() {
        return originalRank;
    }

    public void setOriginalRank(Integer originalRank) {
        this.originalRank = originalRank;
    }

    public Integer getJudgement() {
        return judgement;
    }

    public void setJudgement(Integer judgement) {
        this.judgement = judgement;
    }

    @Override
    public String toString() {
        return String.format("mongoId : %s, originalRank: %d, judgement: %d", this.mongoId, this.originalRank, this.judgement);
    }
}
