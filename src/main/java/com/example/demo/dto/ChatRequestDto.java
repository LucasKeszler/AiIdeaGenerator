package com.example.demo.dto;

import java.util.List;

/**
 * Hele samtalen indtil nu sendes med hver gang, så Claude kan huske konteksten.
 * Den sidste besked skal være brugerens nye spørgsmål.
 */
public record ChatRequestDto(List<ChatMessageDto> messages) {}
