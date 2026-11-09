package com.example.demo.api;

import com.example.demo.dto.FreeApiDto;
import com.example.demo.service.FreePublicApisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/apier")
public class FreeApiController {

    private final FreePublicApisService freePublicApisService;


    public FreeApiController(FreePublicApisService freePublicApisService) {
        this.freePublicApisService = freePublicApisService;
    }

    @GetMapping
    public List<FreeApiDto>  getFreeApis() {
        return freePublicApisService.getFreeApis();
    }

}
