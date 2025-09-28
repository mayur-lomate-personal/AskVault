package com.mayur.askvault.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Component
public class SimpleOpenAIEmbeddingClient implements EmbeddingClient {


    @Value("${llm.api.key:}")
    private String apiKey;


    @Value("${llm.base-url:https://api.openai.com}")
    private String baseUrl;


    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public List<Float> createEmbedding(String model, String text) {
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            String url = baseUrl + "/v1/embeddings";
            HttpPost post = new HttpPost(url);
            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");


            String body = mapper.createObjectNode()
                    .put("model", model)
                    .put("input", text)
                    .toString();
            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));


            var resp = http.execute(post);
            var entity = resp.getEntity();
            if (entity == null) return List.of();
            JsonNode root = mapper.readTree(entity.getContent());
            JsonNode emb = root.at("/data/0/embedding");
            List<Float> vec = new ArrayList<>();
            for (JsonNode n : emb) vec.add((float) n.doubleValue());
            return vec;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
