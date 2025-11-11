package com.vijay.BrainRoutingRagMCP20.controller;

import com.vijay.BrainRoutingRagMCP20.service.AIAgentToolService;
import com.vijay.BrainRoutingRagMCP20.service.ToolFinderService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * The main chat controller that uses the "Smart Finder" (RAG) architecture.
 */
@RestController
public class ChatController {



    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ToolFinderService toolFinder;

    // This is the single, reusable ChatClient instance.
    private final ChatClient chatClient;

    @Autowired
    public ChatController(ToolFinderService toolFinder,
                          AIAgentToolService aiAgentToolService,
                          ChatClient.Builder openAiChatClientBuilder) {

        this.toolFinder = toolFinder;

        // --- THIS IS THE EFFICIENT PATTERN ---
        // 1. Build the ChatClient ONCE in the constructor.
        // 2. Give it ALL your tools (e.g., all 200) just one time.
        //    The .toolNames() call later will filter them for each request.

        // --- THIS IS THE FIX ---
        // The method is .defaultTools() not .tools() on the builder.
        this.chatClient = openAiChatClientBuilder
                .defaultTools(aiAgentToolService) // Registers all @Tool methods from your service
                .build();
    }

    /**
     * Handles a user chat request using the RAG Tool-Finding pattern.
     * @param prompt The user's natural language request.
     * @return The AI's final string response after any tool calls.
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        logger.info("Chat request received: {}", prompt);

        // 1. SMART FINDER (RAG) STEP:
        // Find the *names* of the tools needed (e.g., ["getWeather", "sendEmail"])
        List<String> requiredToolNames = toolFinder.findToolsFor(prompt);
        logger.info("SmartFinder: Activating tools for this request: {}", requiredToolNames);

        // Convert the List<String> to a String[] for the .toolNames() method
        String[] toolNamesArray = requiredToolNames.toArray(new String[0]);

        // 2. "AGENTIC" CALL:
        // Use the single, pre-built chatClient.
        String content = this.chatClient.prompt()
                .user(prompt)

                // --- THIS IS THE CORRECT API CALL ---
                // .toolNames() filters the *entire* tool list (from aiAgentToolService)
                // for this request only. This is the "on-demand" RAG pattern.
                .toolNames(toolNamesArray)

                // .call().content() handles the entire recursive loop
                // (e.g., call tool -> get result -> call another tool)
                // and returns the final text response.
                .call()
                .content();

        logger.info("Final AI response: {}", content);
        return content;
    }
}