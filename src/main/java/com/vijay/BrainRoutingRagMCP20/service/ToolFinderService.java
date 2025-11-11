package com.vijay.BrainRoutingRagMCP20.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore; // <-- This import will now work
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolFinderService {

    private static final Logger logger = LoggerFactory.getLogger(ToolFinderService.class);

    // We can now use the VectorStore INTERFACE
    private final VectorStore vectorStore;

    public ToolFinderService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<String> findToolsFor(String prompt) {
        SearchRequest request = SearchRequest.builder()
                .query(prompt)
                .topK(3)
                .similarityThreshold(0.7)
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(request);

        List<String> toolNames = similarDocuments.stream()
                .map(doc -> (String) doc.getMetadata().get("toolName"))
                .collect(Collectors.toList());

        logger.info("SmartFinder: Found tools {} for prompt: {}", toolNames, prompt);
        return toolNames;
    }
}