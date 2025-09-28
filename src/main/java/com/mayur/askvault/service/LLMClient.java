package com.mayur.askvault.service;

import java.util.List;


public interface LLMClient {
    String generateAnswer(String model, String prompt, List<String> contextChunks);
}
