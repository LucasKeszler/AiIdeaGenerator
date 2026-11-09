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
            FreeApiDto[] apis = restClient.get().uri(freeApisUrl).retrieve().body(FreeApiDto[].class);

            if (apis != null && apis.length > 0) {
                return List.of(apis);
            } else {
                logger.warn("freepublicapis.com returnerede ingen API´er");
                return List.of();
            }
        } catch (RestClientException e) {
            logger.error("Kunne ikke hente api´er", e);
            return List.of();
        }
    }
}