package com.vijay.BrainRoutingRagMCP20.service;

import com.vijay.BrainRoutingRagMCP20.manager.AiToolProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * This is the central "Tool Facade" for our AI.
 * It implements AiToolProvider so the RAG indexer can find it.
 * It wraps all 17 of your existing manager services.
 */
@Service
@RequiredArgsConstructor
public class AIAgentToolService implements AiToolProvider {




}
