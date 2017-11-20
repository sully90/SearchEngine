package com.sully90.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sully90.models.Movie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class UpdateRequest {

    private LinkedList<String> mongoId;
    private LinkedList<Integer> originalRank;
    private LinkedList<Integer> judgement;  // a score between 0 and 3, where 0 is an irrelevant result and 3 is a perfect score

    public UpdateRequest(LinkedList<String> mongoId, LinkedList<Integer> originalRank, LinkedList<Integer> judgement) {
        this.mongoId = mongoId;
        this.originalRank = originalRank;
        this.judgement = judgement;
    }

    private UpdateRequest() {
        // For Jackson
    }

    public LinkedList<String> getMongoId() {
        return mongoId;
    }

    public LinkedList<Integer> getOriginalRank() {
        return originalRank;
    }

    public LinkedList<Integer> getJudgement() {
        return judgement;
    }

    @JsonIgnore
    public float[] discountedGain() {
        float[] discountedGain = new float[this.judgement.size()];

        for (int i = 0; i < this.judgement.size(); i++) {
            discountedGain[i] = (float) this.judgement.get(i) / (float) this.originalRank.get(i);
        }

        return discountedGain;
    }

    @JsonIgnore
    public float[] discountedCumulativeGain() {
        float[] discountedGain = this.discountedGain();
        float[] discountedCumulativeGain = new float[discountedGain.length];

        float sum = 0.0f;
        for (int i = 0; i < discountedGain.length; i++) {
            discountedCumulativeGain[i] = discountedGain[i] + sum;
            sum += discountedGain[i];
        }

        return discountedCumulativeGain;
    }

    @JsonIgnore
    public float[] idealDiscountedCumulativeGain() {
        float[] idealDiscountedCumulativeGain = new float[this.judgement.size()];

        float sum = 0.0f;
        for (int i = 0; i < idealDiscountedCumulativeGain.length; i++) {
            float idealDiscountedGain = idealGain(i, idealDiscountedCumulativeGain.length);
            idealDiscountedCumulativeGain[i] = idealDiscountedGain + sum;
            sum += idealDiscountedGain;
        }

        return idealDiscountedCumulativeGain;
    }

    @JsonIgnore
    public float[] normalizedDiscountedCumulativeGain() {
        float[] discountedCumulativeGain = this.discountedCumulativeGain();
        float[] idealDiscountedCumulativeGain = this.idealDiscountedCumulativeGain();

        float[] normalizedDiscountedCumulativeGain = new float[discountedCumulativeGain.length];

        for (int i = 0; i < discountedCumulativeGain.length; i++) {
            normalizedDiscountedCumulativeGain[i] = discountedCumulativeGain[i] / idealDiscountedCumulativeGain[i];
        }

        return normalizedDiscountedCumulativeGain;
    }

    @JsonIgnore
    public static float idealGain(int rank, int numSamples) {
        final int bestScore = 3;

        return Math.abs( ((float) rank) / ((float) numSamples) - 1 ) * bestScore;
    }

    public static void main(String[] args) {

        Random random = new Random();
        int min = 0;
        int max = 3;

        List<Movie> movies = new ArrayList<>();
        Movie.finder().find("{}", 10).forEach(movies::add);

        System.out.println(movies.size());

        LinkedList<String> ids = new LinkedList<>();
        LinkedList<Integer> originalRanks = new LinkedList<>();
        LinkedList<Integer> judgements = new LinkedList<>();

        for (int i = 0; i < movies.size(); i++) {
            ids.add(movies.get(i).getMongoId());
            judgements.add(Integer.valueOf(random.nextInt((max - min) + 1) + min));
            originalRanks.add(Integer.valueOf(i + 1));
        }

        System.out.println(originalRanks);
        System.out.println(judgements);

        UpdateRequest updateRequest = new UpdateRequest(ids, originalRanks, judgements);

        float[] normalizedDiscountedCumulativeGain = updateRequest.normalizedDiscountedCumulativeGain();
        for (float val : normalizedDiscountedCumulativeGain) {
            System.out.print(val + " ");
        }
        System.out.println();
    }
}
