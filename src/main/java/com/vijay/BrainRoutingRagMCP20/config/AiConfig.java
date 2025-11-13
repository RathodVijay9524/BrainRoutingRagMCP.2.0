package com.vijay.BrainRoutingRagMCP20.config;

import com.vijay.BrainRoutingRagMCP20.service.AIAgentToolService;
import com.vijay.BrainRoutingRagMCP20.service.SelfRefineEvaluationAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI Configuration for Smart Finder RAG
 * 
 * NOTE: ChatClient and InMemoryChatMemory are not available in current Spring AI starters.
 * These will be configured dynamically in ChatController when needed.
 */
@Configuration
public class AiConfig {
    // Configuration placeholder for future use

    @Bean
    public ToolCallingManager toolCallingManager() {
        System.out.println("tool callback working");
        return ToolCallingManager.builder()
                .toolExecutionExceptionProcessor((toolName) -> {
                    System.out.println("--- TOOL EXECUTION FAILED: " + toolName + " ---");
                    return "Tool execution failed: ";
                })

                .build();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel mainModel,
                                 AIAgentToolService aiAgentToolService,
                                 SelfRefineEvaluationAdvisor advisor) {

        return ChatClient.builder(mainModel)
                .defaultAdvisors(advisor)
                .defaultTools(aiAgentToolService)
                .build();
    }
}
