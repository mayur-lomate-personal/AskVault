package com.mayur.askvault.service;

import com.mayur.askvault.model.DocumentEntity;
import com.mayur.askvault.repository.DocumentRepository;
import com.mayur.askvault.util.EmbeddingModelResolver;
import com.mayur.askvault.util.LlmModelResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for handling knowledge base operations.
 */
@Service
public class KnowledgeBaseService {

    private final DocumentRepository repository;
    private final ApplicationContext applicationContext;
    private final VectorStore vectorStore;
    private final EmbeddingModelResolver embeddingModelResolver;
    private final LlmModelResolver llmModelResolver;

    @Autowired
    public KnowledgeBaseService(DocumentRepository repository,
                                VectorStore vectorStore,
                                ApplicationContext applicationContext,
                                EmbeddingModelResolver embeddingModelResolver,
                                LlmModelResolver llmModelResolver) {
        this.repository = repository;
        this.vectorStore = vectorStore;
        this.applicationContext = applicationContext;
        this.embeddingModelResolver = embeddingModelResolver;
        this.llmModelResolver = llmModelResolver;
    }

    /**
     * Adds a document to a knowledge base, generates embeddings, and stores them.
     */
    public DocumentEntity addDocument(String kb, String title, String content, String embeddingModel) {
        EmbeddingService embeddingService = applicationContext.getBean(embeddingModelResolver.resolve(embeddingModel), EmbeddingService.class);

        // Generate embedding for content
        List<Float> embedding = embeddingService.embed(content, embeddingModel);

        // Save document metadata in DB
        DocumentEntity doc = new DocumentEntity();
        doc.setKnowledgeBase(kb);
        doc.setTitle(title);
        doc.setContent(content);
        float[] embeddingArray = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            embeddingArray[i] = embedding.get(i);
        }
        doc.setEmbedding(embeddingArray);
        doc = repository.save(doc);

        return doc;
    }

    /**
     * Queries the knowledge base with a natural-language question.
     */
    public String query(String kb, String model, String embeddingModel, String question, int topK) {
        // Generate embedding for the query
        EmbeddingService embeddingService = applicationContext.getBean(embeddingModelResolver.resolve(embeddingModel), EmbeddingService.class);
        List<Float> queryEmbedding = embeddingService.embed(question, embeddingModel);

        String vectorString = queryEmbedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        // 4️⃣ Fetch top-K documents using pgvector similarity
        List<DocumentEntity> topDocs = repository.findTopKByKnowledgeBaseAndEmbedding(kb, vectorString, topK);

        // Fetch content of those docs
        StringBuilder context = new StringBuilder();
        topDocs.forEach(doc -> {
            context.append("\n---\n")
                    .append(doc.getTitle())
                    .append(":\n")
                    .append(doc.getContent());
        });

        // Ask LLM with context + question
        LlmService llmService = applicationContext.getBean(llmModelResolver.resolve(model), LlmService.class);
        return llmService.ask(model, question, context.toString());
    }
}