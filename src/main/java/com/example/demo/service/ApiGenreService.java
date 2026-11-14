package com.example.demo.service;

import com.example.demo.dto.ApiGenreDto;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


@Service
public class ApiGenreService {

    private final Map<Integer, ApiGenreDto> apiGenres;

    public ApiGenreService(JsonMapper jsonMapper) throws IOException {
        try (InputStream in = new ClassPathResource("genres.json").getInputStream()) {
            apiGenres = jsonMapper.readValue(in, new TypeReference<Map<Integer, ApiGenreDto>>() {});
        }
    }

    public ApiGenreDto getApiGenreById(int id) {
        return apiGenres.getOrDefault(id, new ApiGenreDto("Andet", "Andet", false));
    }
}