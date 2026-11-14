package com.example.demo.service;

import com.example.demo.dto.ApiGenreDto;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


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

    // Alle genrer med deres undergenrer, til frontendens knapper. "Skjult" kommer ikke med.
    // TreeMap og TreeSet sorterer alfabetisk og fjerner dubletter.
    public Map<String, Set<String>> getGenresWithSubgenres() {
        return apiGenres.values().stream()
                .filter(g -> !g.genre().equals("Skjult"))
                .collect(Collectors.groupingBy(
                        ApiGenreDto::genre,
                        TreeMap::new,
                        Collectors.mapping(ApiGenreDto::subgenre, Collectors.toCollection(TreeSet::new))
                ));
    }
}
