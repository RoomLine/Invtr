package com.invtr.reportsservice.client;

import com.invtr.reportsservice.dto.RequestResponse;
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
public class RequestServiceClient {

    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    public RequestServiceClient(@Value("${request.service.url}") String equipmentServiceUrl,
                                HttpServletRequest httpServletRequest) {
        this.restClient = RestClient.builder()
                .baseUrl(equipmentServiceUrl)
                .build();
        this.httpServletRequest = httpServletRequest;
    }

    public List<RequestResponse> getAllRequests(String authHeader) {
        try {
            List<RequestResponse> requests = restClient.get()
                    .uri("/requests/manager")
                    .header("Authorization", authHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RequestResponse>>() {});
            return requests != null ? requests : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch requests from request-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}