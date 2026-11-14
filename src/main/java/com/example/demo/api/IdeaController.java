package com.example.demo.api;

import com.example.demo.dto.IdeasResponseDto;
import com.example.demo.service.IdeaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @GetMapping
    public IdeasResponseDto getIdeas(@RequestParam Set<String> subgenre,
                                     @RequestParam(required = false) String ide,
                                     @RequestParam(defaultValue = "false") boolean visNsfw) {
        return ideaService.getIdeas(subgenre, ide, visNsfw);
    }
}
