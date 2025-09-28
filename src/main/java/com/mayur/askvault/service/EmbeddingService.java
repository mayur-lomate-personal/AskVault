package com.mayur.askvault.service;

import java.util.List;

public interface EmbeddingService {

    List<Float> embed(String content, String embeddingModel);
}
