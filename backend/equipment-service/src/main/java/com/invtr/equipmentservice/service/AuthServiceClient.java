package com.invtr.equipmentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceClient(@Value("${auth.service.url}") String authServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    public List<String> getAdminEmails() {
        try {
            List<String> emails = restClient.get()
                    .uri("/auth/internal/admins/emails")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});
            return emails != null ? emails : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch admin emails from auth-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}