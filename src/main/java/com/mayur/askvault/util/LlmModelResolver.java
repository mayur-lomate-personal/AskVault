package com.mayur.askvault.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "llm")
public class LlmModelResolver {

    private Map<String, String> mapping;

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public String resolve(String model) {
        return mapping.getOrDefault(model, null);
    }
}