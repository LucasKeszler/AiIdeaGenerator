package com.example.demo.service;

import com.example.demo.dto.ApiInfoDto;
import com.example.demo.dto.IdeasResponseDto;
import com.example.demo.dto.ProjectIdeaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Set;


@Service
public class IdeaService {

    private static final Logger logger = LoggerFactory.getLogger(IdeaService.class);

    private final ApiSelectionService apiSelectionService;
    private final PromptService promptService;
    private final ApiAdvisorService apiAdvisorService;
    private final JsonMapper jsonMapper;

    public IdeaService(ApiSelectionService apiSelectionService,
                       PromptService promptService,
                       ApiAdvisorService apiAdvisorService,
                       JsonMapper jsonMapper) {
        this.apiSelectionService = apiSelectionService;
        this.promptService = promptService;
        this.apiAdvisorService = apiAdvisorService;
        this.jsonMapper = jsonMapper;
    }

    public IdeasResponseDto getIdeas(Set<String> subgenres, String ide, boolean visNsfw) {
        List<ApiInfoDto> chosen = apiSelectionService.selectApi(subgenres, visNsfw);
        if (chosen.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Der blev ikke fundet nogen API'er i de valgte undergenrer. Prøv nogle andre.");
        }

        String userMessage = promptService.buildUserMessage(ide, chosen);

        String answer;
        try {
            answer = apiAdvisorService.ask(PromptService.SYSTEM_PROMPT, userMessage);
        } catch (RestClientException e) {
            logger.error("Kaldet til Claude fejlede", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Claude kunne ikke svare lige nu. Prøv igen om lidt.");
        }

        List<ProjectIdeaDto> ideas = parseIdeas(answer);
        return new IdeasResponseDto(ideas, chosen);
    }

    private List<ProjectIdeaDto> parseIdeas(String answer) {
        String json = stripCodeFence(answer);
        try {
            return jsonMapper.readValue(json, new TypeReference<List<ProjectIdeaDto>>() {});
        } catch (JacksonException e) {
            logger.error("Kunne ikke læse Claudes svar som JSON: {}", answer);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Claudes svar kunne ikke læses. Prøv igen.");
        }
    }


    private String stripCodeFence(String text) {
        if (text == null) return "";
        String t = text.trim();
        if (t.startsWith("```")) {
            t = t.replaceFirst("^```[a-zA-Z]*\\s*", "");
            t = t.replaceFirst("\\s*```\\s*$", "");
        }
        return t.trim();
    }
}
