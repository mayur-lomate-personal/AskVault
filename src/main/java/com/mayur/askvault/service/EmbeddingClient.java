package com.mayur.askvault.service;

import java.util.List;


public interface EmbeddingClient {
    /**
     * Create embedding for a text with configured model
     */
    List<Float> createEmbedding(String model, String text);
}