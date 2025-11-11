package com.vijay.BrainRoutingRagMCP20.config;

import com.vijay.BrainRoutingRagMCP20.vectorstore.InMemoryVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {
    
    @Bean
    public InMemoryVectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Create an in-memory vector store with the embedding model
        return new InMemoryVectorStore(embeddingModel);
    }
}
