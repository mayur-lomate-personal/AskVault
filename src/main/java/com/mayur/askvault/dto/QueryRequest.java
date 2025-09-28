package com.mayur.askvault.dto;

public class QueryRequest {

    private String model;
    private String embeddingModel;
    private String question;
    private int topK = 5; // default value if not provided

    public QueryRequest() {
    }

    public QueryRequest(String model, String embeddingModel, String question, int topK) {
        this.model = model;
        this.embeddingModel = embeddingModel;
        this.question = question;
        this.topK = topK;
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }
}
