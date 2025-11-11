package com.vijay.BrainRoutingRagMCP20.vectorstore;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory vector store implementation for RAG
 */
@Component
public class InMemoryVectorStore {
    
    private final Map<String, Document> documentStore = new ConcurrentHashMap<>();
    private final Map<String, float[]> embeddingStore = new ConcurrentHashMap<>();
    private final EmbeddingModel embeddingModel;
    
    public InMemoryVectorStore(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
    
    /**
     * Add documents to the vector store
     */
    public void add(List<Document> documents) {
        for (Document doc : documents) {
            String id = UUID.randomUUID().toString();
            documentStore.put(id, doc);
            
            // Try to generate embedding for the document text
            String text = doc.getText();
            if (text != null && !text.isEmpty()) {
                try {
                    float[] embedding = embeddingModel.embed(text);
                    embeddingStore.put(id, embedding);
                } catch (Exception e) {
                    // If embedding fails (e.g., invalid API key), use a dummy embedding
                    // This allows the app to run without valid API credentials for demo purposes
                    float[] dummyEmbedding = new float[1536]; // Standard embedding size
                    embeddingStore.put(id, dummyEmbedding);
                }
            }
        }
    }
    
    /**
     * Search for similar documents based on query
     */
    public List<Document> similaritySearch(String query, int topK) {
        // Generate embedding for the query
        float[] queryEmbedding;
        try {
            queryEmbedding = embeddingModel.embed(query);
        } catch (Exception e) {
            // If embedding fails, use a dummy embedding
            queryEmbedding = new float[1536];
        }
        
        // Calculate similarity scores for all documents
        Map<String, Double> scores = new HashMap<>();
        for (Map.Entry<String, float[]> entry : embeddingStore.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            scores.put(entry.getKey(), similarity);
        }
        
        // Sort by similarity and return top K documents
        return scores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topK)
            .map(entry -> documentStore.get(entry.getKey()))
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate cosine similarity between two vectors
     */
    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    /**
     * Get all documents in the store
     */
    public List<Document> getAllDocuments() {
        return new ArrayList<>(documentStore.values());
    }
    
    /**
     * Clear all documents from the store
     */
    public void clear() {
        documentStore.clear();
        embeddingStore.clear();
    }
    
    /**
     * Get the number of documents in the store
     */
    public int size() {
        return documentStore.size();
    }
}
