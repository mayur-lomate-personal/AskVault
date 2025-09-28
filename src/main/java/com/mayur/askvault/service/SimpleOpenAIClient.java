package com.mayur.askvault.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.util.List;


@Component
public class SimpleOpenAIClient implements LLMClient {
    @Value("${llm.api.key:}")
    private String apiKey;


    @Value("${llm.base-url:https://api.openai.com}")
    private String baseUrl;


    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String generateAnswer(String model, String prompt, List<String> contextChunks) {
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            String url = baseUrl + "/v1/chat/completions";
            HttpPost post = new HttpPost(url);
            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");


// Build a simple system + user messages payload including context
            StringBuilder userSb = new StringBuilder();
            for (String c : contextChunks) {
                userSb.append("Context:\n").append(c).append("\n---\n");
            }
            userSb.append("User question: ").append(prompt);


            var bodyNode = mapper.createObjectNode();
            bodyNode.put("model", model);
            var messages = mapper.createArrayNode();
            messages.add(mapper.createObjectNode().put("role", "system").put("content", "You are a helpful assistant."));
            messages.add(mapper.createObjectNode().put("role", "user").put("content", userSb.toString()));
            bodyNode.set("messages", messages);


            post.setEntity(new StringEntity(mapper.writeValueAsString(bodyNode), StandardCharsets.UTF_8));


            var resp = http.execute(post);
            var entity = resp.getEntity();
            var root = mapper.readTree(entity.getContent());
            var text = root.at("/choices/0/message/content").asText();
            return text;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
