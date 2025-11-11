package com.vijay.BrainRoutingRagMCP20.controller;

import com.vijay.BrainRoutingRagMCP20.service.ToolFinderService;
import com.vijay.BrainRoutingRagMCP20.tools.MyApplicationTools;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    private final ToolFinderService toolFinder;
    private final MyApplicationTools myApplicationTools;
    private final OpenAiChatModel chatModel;

    @Autowired
    public ChatController(ToolFinderService toolFinder, MyApplicationTools myApplicationTools, OpenAiChatModel chatModel) {
        this.toolFinder = toolFinder;
        this.myApplicationTools = myApplicationTools;
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        logger.info("Chat request: {}", prompt);
        
        // 1. SMART FINDER (RAG) STEP:
        // Find the *names* of the tools needed using semantic search
        List<String> requiredToolNames = toolFinder.findToolsFor(prompt);
        logger.info("Found tools: {}", requiredToolNames);

        // 2. TOOL EXECUTION:
        // Execute the selected tools based on the prompt
        StringBuilder toolResults = new StringBuilder();
        
        for (String toolName : requiredToolNames) {
            try {
                if ("getWeather".equals(toolName)) {
                    // Execute getWeather tool
                    MyApplicationTools.WeatherRequest req = new MyApplicationTools.WeatherRequest("New York");
                    MyApplicationTools.WeatherResponse result = myApplicationTools.getWeather(req);
                    toolResults.append("Weather for ").append(result.location()).append(": ")
                            .append(result.temp()).append("°C, ").append(result.conditions()).append("\n");
                } else if ("sendEmail".equals(toolName)) {
                    // Execute sendEmail tool
                    MyApplicationTools.EmailRequest req = new MyApplicationTools.EmailRequest("user@example.com", "Hello from Smart Finder!");
                    MyApplicationTools.EmailResponse result = myApplicationTools.sendEmail(req);
                    toolResults.append("Email sent: ").append(result.message()).append("\n");
                }
            } catch (Exception e) {
                logger.error("Error executing tool: {}", toolName, e);
                toolResults.append("Error executing ").append(toolName).append(": ").append(e.getMessage()).append("\n");
            }
        }

        // 3. LLM RESPONSE:
        // Send tool results to LLM to generate a natural language response
        String llmPrompt = "User asked: " + prompt + "\n\n" +
                          "Tool results:\n" + toolResults + "\n" +
                          "Please provide a helpful response based on these tool results.";
        
        try {
            String llmResponse = chatModel.call(llmPrompt);
            return "Smart Finder selected tools: " + requiredToolNames + "\n\n" +
                   "Tool Results:\n" + toolResults + "\n" +
                   "LLM Response:\n" + llmResponse;
        } catch (Exception e) {
            logger.error("Error calling LLM", e);
            return "Smart Finder selected tools: " + requiredToolNames + "\n\n" +
                   "Tool Results:\n" + toolResults + "\n" +
                   "LLM Error: " + e.getMessage();
        }
    }
}
