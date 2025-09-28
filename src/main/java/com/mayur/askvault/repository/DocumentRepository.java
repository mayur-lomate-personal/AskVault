package com.mayur.askvault.repository;

import com.mayur.askvault.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query(value = """
        SELECT *
        FROM documents
        WHERE knowledge_base = :kb
        ORDER BY embedding <-> CAST(:queryVector AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<DocumentEntity> findTopKByKnowledgeBaseAndEmbedding(
            @Param("kb") String knowledgeBase,
            @Param("queryVector") String queryVector,
            @Param("topK") int topK
    );
}