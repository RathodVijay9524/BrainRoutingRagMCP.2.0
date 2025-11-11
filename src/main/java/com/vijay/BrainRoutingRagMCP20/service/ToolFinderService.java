package com.vijay.BrainRoutingRagMCP20.service;

import com.vijay.BrainRoutingRagMCP20.vectorstore.InMemoryVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolFinderService {

    private static final Logger logger = LoggerFactory.getLogger(ToolFinderService.class);
    private final InMemoryVectorStore vectorStore;

    public ToolFinderService(InMemoryVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Uses RAG to find the most relevant tool names for a prompt.
     */
    public List<String> findToolsFor(String prompt) {
        
        // 1. Search the RAG index for the top 5 matching tools
        List<Document> similarDocuments = vectorStore.similaritySearch(prompt, 5);

        // 2. Filter tools based on keyword matching in the prompt
        List<String> toolNames = similarDocuments.stream()
                .map(doc -> (String) doc.getMetadata().get("toolName"))
                .filter(toolName -> isRelevantTool(toolName, prompt))
                .collect(Collectors.toList());
                
        logger.info("SmartFinder: Found tools {} for prompt: {}", toolNames, prompt);
        return toolNames;
    }
    
    /**
     * Check if a tool is relevant to the prompt using keyword matching
     */
    private boolean isRelevantTool(String toolName, String prompt) {
        String lowerPrompt = prompt.toLowerCase();
        
        if ("getWeather".equals(toolName)) {
            return lowerPrompt.contains("weather") || 
                   lowerPrompt.contains("temperature") || 
                   lowerPrompt.contains("climate") ||
                   lowerPrompt.contains("forecast") ||
                   lowerPrompt.contains("rain") ||
                   lowerPrompt.contains("sunny") ||
                   lowerPrompt.contains("cloudy");
        } else if ("sendEmail".equals(toolName)) {
            return lowerPrompt.contains("email") || 
                   lowerPrompt.contains("send") || 
                   lowerPrompt.contains("message") ||
                   lowerPrompt.contains("contact") ||
                   lowerPrompt.contains("notify") ||
                   lowerPrompt.contains("mail");
        }
        
        return false;
    }
}
