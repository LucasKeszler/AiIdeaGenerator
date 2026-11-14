package com.example.demo.dto;

import java.util.List;


public record IdeasResponseDto(
        List<ProjectIdeaDto> ideas,
        List<ApiInfoDto> apis
) {}
