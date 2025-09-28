package com.mayur.askvault.service;

import com.mayur.askvault.dto.OpenAIEmbeddingRequest;
import com.mayur.askvault.dto.OpenAIEmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

@Service("openai-embedding")
public class OpenAIEmbeddingService implements EmbeddingService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Float> embed(String content, String embeddingModel) {
        OpenAIEmbeddingRequest request = new OpenAIEmbeddingRequest(embeddingModel, content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAIEmbeddingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OpenAIEmbeddingResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                OpenAIEmbeddingResponse.class
        );

        return response.getBody().data.get(0).embedding;
    }
}
