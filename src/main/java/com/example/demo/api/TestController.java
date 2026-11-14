package com.example.demo.api;
import com.example.demo.dto.ApiInfoDto;
import com.example.demo.service.ApiSelectionService;
import com.example.demo.service.PromptService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final ApiSelectionService apiSelectionService;
    private final PromptService promptService;

    public TestController(ApiSelectionService apiSelectionService, PromptService promptService) {
        this.promptService = promptService;
        this.apiSelectionService = apiSelectionService;
    }

    @GetMapping
    public String test() {
        return "test";
    }


    @GetMapping("/apis")
    public List<ApiInfoDto> testApis(@RequestParam Set<String> apiSubGenre, @RequestParam(defaultValue = "false") boolean visNsfw) {
        return apiSelectionService.selectApi(apiSubGenre, visNsfw);
    }

    @GetMapping("/prompt")
    public String testPrompt(@RequestParam Set<String> subgenre,
                             @RequestParam(defaultValue = "false") boolean visNsfw,
                             @RequestParam(required = false) String ide) {

        List<ApiInfoDto> chosen = apiSelectionService.selectApi(subgenre, visNsfw);
        return promptService.buildUserMessage(ide, chosen);
    }

}