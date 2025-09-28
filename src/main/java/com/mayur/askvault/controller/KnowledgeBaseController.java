package com.mayur.askvault.controller;

import com.mayur.askvault.dto.QueryRequest;
import com.mayur.askvault.model.DocumentEntity;
import com.mayur.askvault.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing knowledge bases.
 * Provides APIs to add documents and query answers from a knowledge base.
 */
@RestController
@RequestMapping("/api/kb")
public class KnowledgeBaseController {

    private final KnowledgeBaseService kbService;

    @Autowired
    public KnowledgeBaseController(KnowledgeBaseService kbService) {
        this.kbService = kbService;
    }

    /**
     * Add a new document to a knowledge base.
     *
     * Example:
     * POST /api/kb/hr-policy/documents?title=leave-policy&embeddingModel=text-embedding-3-small
     * Body: (plain text document content)
     */
    @PostMapping("/{kb}/documents")
    public ResponseEntity<DocumentEntity> addDocument(@PathVariable("kb") String kb,
                                                      @RequestParam("title") String title,
                                                      @RequestParam(name = "embeddingModel") String embeddingModel,
                                                      @RequestBody String content) {
        var doc = kbService.addDocument(kb, title, content, embeddingModel);
        return ResponseEntity.ok(doc);
    }

    /**
     * Query a knowledge base with a natural language question.
     *
     * Example:
     * POST /api/kb/hr-policy/query
     * Body:
     * {
     *   "model": "gpt-4o-mini",
     *   "embeddingModel": "text-embedding-3-small",
     *   "question": "How many paid leaves can an employee take?",
     *   "topK": 3
     * }
     */
    @PostMapping("/{kb}/query")
    public ResponseEntity<String> query(@PathVariable("kb") String kb,
                                        @RequestBody QueryRequest req) {
        var answer = kbService.query(
                kb,
                req.getModel(),
                req.getEmbeddingModel(),
                req.getQuestion(),
                req.getTopK()
        );
        return ResponseEntity.ok(answer);
    }
}