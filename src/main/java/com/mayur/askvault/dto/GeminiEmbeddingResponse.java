package com.mayur.askvault.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiEmbeddingResponse {
    public Embedding embedding;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedding {
        @JsonProperty("values")
        public List<Float> values;
    }
}

