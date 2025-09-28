package com.mayur.askvault.service;

import com.mayur.askvault.model.DocumentEntity;

import java.util.List;


public interface VectorStore {
    void upsert(DocumentEntity doc, List<Float> vector);
    List<DocumentEntity> query(String knowledgeBase, List<Float> queryVector, int topK);
}