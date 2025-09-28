package com.mayur.askvault.service;

import com.mayur.askvault.model.DocumentEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryVectorStore implements VectorStore {

    // Map: knowledgeBase -> list of (doc, vector)
    private final Map<String, List<Pair>> store = new ConcurrentHashMap<>();

    private record Pair(DocumentEntity doc, List<Float> vec) {}

    @Override
    public void upsert(DocumentEntity doc, List<Float> vector) {
        store.compute(doc.getKnowledgeBase(), (kb, list) -> {
            if (list == null) list = new ArrayList<>();
            // replace if doc already exists
            list.removeIf(p -> p.doc.getId() != null && p.doc.getId().equals(doc.getId()));
            list.add(new Pair(doc, vector));
            return list;
        });
    }

    @Override
    public List<DocumentEntity> query(String knowledgeBase, List<Float> queryVector, int topK) {
        var list = store.getOrDefault(knowledgeBase, List.of());
        return list.stream()
                .map(p -> Map.entry(p.doc, cosineSim(queryVector, p.vec)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double cosineSim(List<Float> a, List<Float> b) {
        if (a == null || b == null || a.size() != b.size()) return -1;
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.size(); i++) {
            double av = a.get(i), bv = b.get(i);
            dot += av * bv;
            na += av * av;
            nb += bv * bv;
        }
        if (na == 0 || nb == 0) return -1;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}