package com.mayur.askvault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayur.askvault.dto.GeminiEmbeddingRequest;
import com.mayur.askvault.dto.GeminiEmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service("gemini-embedding")
public class GeminiEmbeddingService implements EmbeddingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Float> embed(String content, String embeddingModel) {
        GeminiEmbeddingRequest request = new GeminiEmbeddingRequest(
                new GeminiEmbeddingRequest.Content(Arrays.asList(new GeminiEmbeddingRequest.Part(content))),
                1536
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Sending JSON: " + request);

        HttpEntity<GeminiEmbeddingRequest> entity = new HttpEntity<>(request, headers);

        String url = String.format("%s/models/%s:embedContent", apiUrl, embeddingModel);

        ResponseEntity<GeminiEmbeddingResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                GeminiEmbeddingResponse.class
        );

        if (response.getBody() == null || response.getBody().embedding == null) {
            throw new RuntimeException("Gemini embedding API returned empty response");
        }

        return response.getBody().embedding.values;
    }
}
