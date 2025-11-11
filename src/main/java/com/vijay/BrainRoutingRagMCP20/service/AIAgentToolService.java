package com.vijay.BrainRoutingRagMCP20.service;

import com.vijay.BrainRoutingRagMCP20.manager.AiToolProvider;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * This is our single, unified service that provides ALL AI tools.
 * It implements the AiToolProvider interface so the indexer can find it.
 */
@Service
public class AIAgentToolService implements AiToolProvider {

    // --- DTOs (Data Objects) for our tools ---
    public record WeatherRequest(String location) {}
    public record WeatherResponse(String location, double temp, String conditions) {}
    public record EmailRequest(String to, String body) {}
    public record EmailResponse(boolean success, String message) {}
    public record AddRequest(int a, int b) {}
    public record AddResponse(int result) {}
    public record MultiplyRequest(int a, int b) {}
    public record MultiplyResponse(int result) {}

    // --- TOOL 1: Normal Method ---
    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    // --- TOOL 2: "add" (Corrected) ---
    // Changed from a Function-returning method to a normal method.
    @Tool(description ="Adds two numbers together.")
    public AddResponse add(AddRequest request) {
        System.out.println("--- TOOL CALLED: add(" + request.a() + ", " + request.b() + ") ---");
        return new AddResponse(request.a() + request.b());
    }

    // --- TOOL 3: "multiply" (Corrected) ---
    // Changed from a Function-returning method to a normal method.
    @Tool(description ="Multiplies two numbers together.")
    public MultiplyResponse multiply(MultiplyRequest request) {
        System.out.println("--- TOOL CALLED: multiply(" + request.a() + ", " + request.b() + ") ---");
        return new MultiplyResponse(request.a() * request.b());
    }

    // --- TOOL 4: Normal Method ---
    @Tool(description = "Get the current weather for a specific city")
    public WeatherResponse getWeather(WeatherRequest request) {
        System.out.println("--- AI TOOL: Getting weather for " + request.location());
        return new WeatherResponse(request.location(), 15.5, "Cloudy");
    }

    // --- TOOL 5: Normal Method ---
    @Tool(description = "Send an email to a recipient")
    public EmailResponse sendEmail(EmailRequest request) {
        System.out.println("--- AI TOOL: Sending email to " + request.to());
        return new EmailResponse(true, "Email sent successfully to " + request.to());
    }
}