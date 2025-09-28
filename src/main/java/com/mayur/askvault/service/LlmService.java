package com.mayur.askvault.service;

public interface LlmService {
    String ask(String model, String question, String context);
}
