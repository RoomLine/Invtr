package com.invtr.reportsservice.client;

import com.invtr.reportsservice.dto.UsersResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest httpServletRequest;

    public AuthServiceClient(@Value("${auth.service.url}") String authServiceUrl,
                             HttpServletRequest httpServletRequest) {
        this.restClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .build();
        this.httpServletRequest = httpServletRequest;
    }

    public List<UsersResponse> getAllUsers() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        try {
            List<UsersResponse> users = restClient.get()
                    .uri("/auth/users")
                    .header("Authorization", authHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UsersResponse>>() {});
            return users != null ? users : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch users from auth-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}