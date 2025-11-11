package com.vijay.BrainRoutingRagMCP20.service;

import com.vijay.BrainRoutingRagMCP20.manager.AiToolProvider;
import com.vijay.BrainRoutingRagMCP20.vectorstore.InMemoryVectorStore;
import com.vijay.BrainRoutingRagMCP20.tools.MyApplicationTools;
import org.springframework.ai.document.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@Service
public class ToolIndexingService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ToolIndexingService.class);

    private final InMemoryVectorStore vectorStore;
    private final List<AiToolProvider> allToolProviders;

    public ToolIndexingService(InMemoryVectorStore vectorStore, List<AiToolProvider> allToolProviders) {
        this.vectorStore = vectorStore;
        this.allToolProviders = allToolProviders;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("--- Indexing all tools from {} providers... ---", allToolProviders.size());
        
        List<Document> toolDocuments = new java.util.ArrayList<>();

        // Loop through each AiToolProvider and manually index tools
        for (AiToolProvider provider : allToolProviders) {
            if (provider instanceof MyApplicationTools) {
                // Index getWeather tool
                Document weatherDoc = new Document(
                    "Get the current weather for a specific city",
                    Map.of("toolName", "getWeather", "class", "MyApplicationTools")
                );
                toolDocuments.add(weatherDoc);
                logger.info("Indexing tool: getWeather");
                
                // Index sendEmail tool
                Document emailDoc = new Document(
                    "Send an email to a recipient",
                    Map.of("toolName", "sendEmail", "class", "MyApplicationTools")
                );
                toolDocuments.add(emailDoc);
                logger.info("Indexing tool: sendEmail");
            }
        }
        
        // Add all tool documents to the RAG database
        if (!toolDocuments.isEmpty()) {
            vectorStore.add(toolDocuments);
            logger.info("--- Indexed {} tools to the Vector Store. ---", toolDocuments.size());
        }
    }
}
