package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FreeApiDto(Integer id, String emoji, String title, String description, String documentation, String source, Integer health) {}

