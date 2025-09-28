package com.mayur.askvault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIEmbeddingResponse {
    public List<Data> data;

    public static class Data {
        public List<Float> embedding;
        public String object;
    }
}
