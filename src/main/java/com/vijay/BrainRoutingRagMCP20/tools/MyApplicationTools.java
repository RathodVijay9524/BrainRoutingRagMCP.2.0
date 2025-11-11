package com.vijay.BrainRoutingRagMCP20.tools;

import com.vijay.BrainRoutingRagMCP20.manager.AiToolProvider;
import org.springframework.stereotype.Service;

@Service
public class MyApplicationTools implements AiToolProvider {

    // DTOs (Data Objects) for our tools
    public record WeatherRequest(String location) {}
    public record WeatherResponse(String location, double temp, String conditions) {}
    public record EmailRequest(String to, String body) {}
    public record EmailResponse(boolean success, String message) {}

    /**
     * Tool 1: A "normal method" to get the weather.
     * Description: Get the current weather for a specific city
     */
    public WeatherResponse getWeather(WeatherRequest request) {
        System.out.println("--- AI TOOL: Getting weather for " + request.location());
        // Dummy implementation
        return new WeatherResponse(request.location(), 15.5, "Cloudy");
    }

    /**
     * Tool 2: A "normal method" to send an email.
     * Description: Send an email to a recipient
     */
    public EmailResponse sendEmail(EmailRequest request) {
        System.out.println("--- AI TOOL: Sending email to " + request.to());
        // Dummy implementation
        return new EmailResponse(true, "Email sent successfully to " + request.to());
    }
}
