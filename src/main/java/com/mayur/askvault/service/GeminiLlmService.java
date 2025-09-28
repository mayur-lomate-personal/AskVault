package com.mayur.askvault.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("gemini-llm")
public class GeminiLlmService implements LlmService {

    private final Client geminiClient;

    public GeminiLlmService(@Value("${gemini.api.key}") String apiKey) {
        this.geminiClient = Client.builder().apiKey(apiKey).build();
    }

    @Override
    public String ask(String model, String question, String context) {
        // Build a compact prompt that includes retrieved context + question.
        // You can customize system instructions, verbosity, etc.
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful assistant. Use the context below to answer the question.\n\n");
        if (context != null && !context.isBlank()) {
            prompt.append("Context:\n").append(context).append("\n\n");
        }
        prompt.append("Question: ").append(question).append("\n\n");
        prompt.append("Answer succinctly and clearly. If the answer is not known, say \"I don't know.\"");

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        model,
                        prompt.toString(),
                        null);

        String finalAnswer = response.text();

        return (finalAnswer == null || finalAnswer.isBlank())
                ? "Sorry, I could not get an answer from Gemini."
                : finalAnswer.trim();
    }
}
