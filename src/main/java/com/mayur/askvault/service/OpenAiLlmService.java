package com.mayur.askvault.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.ResponseOutputText;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("openai-llm")
public class OpenAiLlmService implements LlmService {

    private final OpenAIClient openAiClient;

    public OpenAiLlmService(@Value("${openai.api.key}") String apiKey) {
        this.openAiClient = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
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

        // Build request for the Responses API
        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt.toString())
                // pass the model id string; the SDK supports model identifiers here.
                .model(model)
                // you may set other params (e.g., maxOutputTokens, temperature) if desired:
                // .maxOutputTokens(500)
                // .temperature(0.0)
                .build();

        Response response = openAiClient.responses().create(params);

        // Extract textual output from the Response object.
        // The Responses API returns a structured output array. Walk it and collect any outputText elements.
        StringBuilder answer = new StringBuilder();

        List<ResponseOutputItem> outputs = response.output();
        if (outputs != null) {
            for (ResponseOutputItem item : outputs) {
                // each item may have one or more messages (normally assistant messages)
                Optional<ResponseOutputMessage> messages = item.message();
                if (messages == null) continue;
                for (ResponseOutputMessage message : messages.stream().toList()) {
                    List<ResponseOutputMessage.Content> contents = message.content();
                    if (contents == null) continue;
                    for (ResponseOutputMessage.Content content : contents) {
                        // content.outputText() is a list of text chunks (if present)
                        Optional<ResponseOutputText> texts = content.outputText();
                        if (texts == null) continue;
                        for (ResponseOutputText t : texts.stream().toList()) {
                            String txt = t.text();
                            if (txt != null && !txt.isBlank()) {
                                // append with a newline separator (trim later)
                                answer.append(txt).append("\n");
                            }
                        }
                    }
                }
            }
        }

        String finalAnswer = answer.toString().trim();
        if (finalAnswer.isEmpty()) {
            // as a fallback, try response.outputText() or response.outputTextDelta if available:
            // Many client versions expose convenience getters like response.outputText() or response.outputTextDelta(),
            // but the above traversal is robust for the structured types the SDK returns.
            Optional<String> fallback = Optional.empty();
            try {
                // try conveninence call (if available in this SDK version)
                // some SDK versions expose response.outputText(); guard with reflection fallback.
                var method = response.getClass().getMethod("outputText");
                Object o = method.invoke(response);
                if (o instanceof String) fallback = Optional.of((String) o);
            } catch (Exception ignored) { /* ignore reflection fallback */ }

            return fallback.orElse("Sorry, I could not get an answer from the LLM.");
        }

        return finalAnswer;
    }
}