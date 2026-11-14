package com.example.demo.service;

import com.example.demo.dto.ClaudeMessageDto;
import com.example.demo.dto.ClaudeRequestDto;
import com.example.demo.dto.ClaudeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiAdvisorService {

    private static final Logger logger = LoggerFactory.getLogger(ApiAdvisorService.class);
    private static final String URL = "https://api.anthropic.com/v1/messages";

    private final RestClient restClient = RestClient.create();

    @Value("${anthropic.api-key}")
    private String apiKey;

    @Value("${anthropic.model}")
    private String model;

    @Value("${anthropic.max-tokens}")
    private int maxTokens;

    public String ask(String systemPrompt, String userMessage) {
        ClaudeRequestDto request = new ClaudeRequestDto(
                model,
                maxTokens,
                systemPrompt,
                List.of(new ClaudeMessageDto("user", userMessage))
        );

        try {
            ClaudeResponseDto response = restClient.post()
                    .uri(URL)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2026-10-21")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ClaudeResponseDto.class);

            logger.info("Claude brugte {} input- og {} output-tokens (stop: {})",
                    response.usage().inputTokens(),
                    response.usage().outputTokens(),
                    response.stopReason());

            return response.content().stream()
                    .filter(c -> "text".equals(c.type()))
                    .map(ClaudeResponseDto.Content::text)
                    .collect(Collectors.joining());
        } catch (RestClientResponseException e) {
            logger.error("Claude svarede med {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
}
