package com.example.demo.dto;

/**
 * En enkelt besked i samtalen.
 * role er enten "user" eller "assistant".
 */
public record ChatMessageDto(String role, String content) {}
