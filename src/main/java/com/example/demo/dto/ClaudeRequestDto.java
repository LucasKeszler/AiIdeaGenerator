package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ClaudeRequestDto(String model, @JsonProperty("max_tokens") int maxTokens, String system, List<ClaudeMessageDto> messages) {
}
