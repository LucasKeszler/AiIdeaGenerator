package com.example.demo.service;

import com.example.demo.dto.ApiGenreDto;
import com.example.demo.dto.ApiInfoDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiSelectionService {

    private final ApiGenreService apiGenreService;
    private final FreePublicApisService freePublicApisService;


    public ApiSelectionService(FreePublicApisService freePublicApisService, ApiGenreService apiGenreService) {
        this.freePublicApisService = freePublicApisService;
        this.apiGenreService = apiGenreService;
    }

    public List<ApiInfoDto> getAllApi() {
        return freePublicApisService.getFreeApis().stream()
                .map(api -> {
                    ApiGenreDto g = apiGenreService.getApiGenreById(api.id());
                    return new ApiInfoDto(api.id(), api.emoji(), api.title(), api.description(), api.documentation(), api.source(), api.health(), g.genre(), g.subgenre(), Boolean.TRUE.equals(g.nsfw()));
                })
                .toList();
    }

    private static final int MIN_API_HEALTH = 50;
    private static final int MAX_API = 15;

    public List<ApiInfoDto> selectApi(Set<String> apiSubGenres, boolean visNsfw) {
        if (apiSubGenres == null || apiSubGenres.isEmpty()) {
            return List.of();
        }
        int perSubGenre = Math.max(1, MAX_API / apiSubGenres.size());

        return getAllApi().stream()
                .filter(a -> !a.genre().equals("Skjult"))
                .filter(a -> apiSubGenres.contains(a.subgenre()))
                .filter(a -> visNsfw || !a.nsfw())
                .filter(a -> a.health() == null || a.health() >= MIN_API_HEALTH)
                .collect(Collectors.groupingBy(ApiInfoDto::subgenre))
                .values().stream()
                .flatMap(group -> group.stream()
                        .sorted(Comparator.comparing(ApiInfoDto::health,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(perSubGenre))
                .toList();
    }
}