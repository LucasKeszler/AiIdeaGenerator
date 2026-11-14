package com.example.demo.api;

import com.example.demo.service.ApiGenreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    private final ApiGenreService apiGenreService;

    public GenreController(ApiGenreService apiGenreService) {
        this.apiGenreService = apiGenreService;
    }


    @GetMapping
    public Map<String, Set<String>> getGenres() {
        return apiGenreService.getGenresWithSubgenres();
    }
}
