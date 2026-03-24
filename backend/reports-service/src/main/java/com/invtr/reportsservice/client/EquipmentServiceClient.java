package com.invtr.reportsservice.client;

import com.invtr.reportsservice.dto.EquipmentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class EquipmentServiceClient {

    private final RestClient restClient;
    private final HttpServletRequest httpServletRequest;

    public EquipmentServiceClient(@Value("${equipment.service.url}") String equipmentServiceUrl,
                                  HttpServletRequest httpServletRequest) {
        this.restClient = RestClient.builder()
                .baseUrl(equipmentServiceUrl)
                .build();
        this.httpServletRequest = httpServletRequest;
    }

    public List<EquipmentResponse> getAllEquipment(String authHeader) {
        try {
            List<EquipmentResponse> equipment = restClient.get()
                    .uri("/equipment")
                    .header("Authorization", authHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<EquipmentResponse>>() {});
            return equipment != null ? equipment : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch equipment from equipment-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}