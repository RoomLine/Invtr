package com.invtr.equipmentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public List<String> getAdminEmails() {
        return RestClient.create()
                .get()
                .uri(authServiceUrl + "/internal/admins/emails")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }
}