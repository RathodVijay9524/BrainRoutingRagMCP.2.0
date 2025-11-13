package com.vijay.BrainRoutingRagMCP20.controller;

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

    //http://localhost:9092/chat?prompt=What%20is%20the%20weather%20in%20London%3F
    //http://localhost:9092/chat?prompt=Please%20send%20an%20email%20to%20vijay%40example.com%20saying%20'the%20RAG%20system%20is%20working'
    //http://localhost:9092/chat?prompt=what%20is%20123%20plus%20456%3F
    //http://localhost:9092/chat?prompt=what%20is%20the%20date%20and%20time%20right%20now%3F

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ToolFinderService toolFinder;

    // This is the single, reusable ChatClient instance.
    private final ChatClient chatClient;

    @Autowired
    public ChatController(ToolFinderService toolFinder,
                         ChatClient chatClient) {
        this.toolFinder = toolFinder;
        this.chatClient = chatClient;
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
                .toolNames(toolNamesArray)
                .call()
                .content();

        logger.info("Final AI response: {}", content);
        return content;
    }
}