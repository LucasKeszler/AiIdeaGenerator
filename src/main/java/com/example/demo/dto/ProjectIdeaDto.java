package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectIdeaDto(
        String titel,
        String beskrivelse,
        String hovedApi,
        List<String> alternativer,
        List<String> features
) {}
