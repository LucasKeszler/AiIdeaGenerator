package com.example.demo.service;

import com.example.demo.dto.FreeApiDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class FreePublicApisService {

    private static final Logger logger = LoggerFactory.getLogger(FreePublicApisService.class);

    @Value("${app.freeapis.url}")
    private String freeApisUrl;

    private final RestClient restClient = RestClient.create();

    public List<FreeApiDto> getFreeApis() {
        try {
            FreeApiDto[] apis = restClient.get()
                    .uri(freeApisUrl)
                    .retrieve()
                    .body(FreeApiDto[].class);

            return List.of(apis);
        } catch (RestClientException e) {
            logger.error("Kunne ikke hente api´er", e);
            return List.of();
        }
    }
}
