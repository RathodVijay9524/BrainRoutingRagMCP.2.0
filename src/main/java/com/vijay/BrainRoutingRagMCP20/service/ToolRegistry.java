package com.vijay.BrainRoutingRagMCP20.service;

import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry to store tool metadata and methods for dynamic invocation
 */
@Component
public class ToolRegistry {
    
    private final Map<String, ToolMetadata> toolRegistry = new ConcurrentHashMap<>();
    
    public static class ToolMetadata {
        public final String toolName;
        public final String description;
        public final Method method;
        public final Object instance;
        
        public ToolMetadata(String toolName, String description, Method method, Object instance) {
            this.toolName = toolName;
            this.description = description;
            this.method = method;
            this.instance = instance;
        }
    }
    
    /**
     * Register a tool
     */
    public void registerTool(String toolName, String description, Method method, Object instance) {
        toolRegistry.put(toolName, new ToolMetadata(toolName, description, method, instance));
    }
    
    /**
     * Get tool metadata by name
     */
    public ToolMetadata getTool(String toolName) {
        return toolRegistry.get(toolName);
    }
    
    /**
     * Get all registered tools
     */
    public Collection<ToolMetadata> getAllTools() {
        return toolRegistry.values();
    }
    
    /**
     * Check if tool exists
     */
    public boolean hasTool(String toolName) {
        return toolRegistry.containsKey(toolName);
    }
    
    /**
     * Get tool count
     */
    public int getToolCount() {
        return toolRegistry.size();
    }
}
