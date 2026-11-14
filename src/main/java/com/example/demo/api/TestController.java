package com.example.demo.api;
import com.example.demo.dto.ApiInfoDto;
import com.example.demo.service.ApiSelectionService;
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

    public TestController(ApiSelectionService apiSelectionService) {
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

}