package com.invtr.requestservice.service;

import com.invtr.requestservice.client.dto.*;
import com.invtr.requestservice.client.enums.EquipmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
public class EquipmentServiceClient {

	private final RestClient restClient;

	public EquipmentServiceClient(@Value("${equipment.service.url}") String equipmentServiceUrl) {
		this.restClient = RestClient.builder()
				.baseUrl(equipmentServiceUrl)
				.build();
	}

	public EquipmentResponse getEquipment(Long id, String authorizationHeader) {
		try {
			return restClient.get()
					.uri("/equipment/{id}", id)
					.header("Authorization", authorizationHeader)
					.retrieve()
					.body(EquipmentResponse.class);
		} catch (RestClientException e) {
			log.error("GET /equipment/{} failed: {}", id, e.getMessage());
			throw e;
		}
	}

	public void updateEquipmentStatus(Long equipmentId, EquipmentStatus status, String authorizationHeader) {
		var body = UpdateEquipmentStatusRequest.builder().status(status).build();
		try {
			restClient.patch()
					.uri("/equipment/{id}/status", equipmentId)
					.header("Authorization", authorizationHeader)
					.contentType(MediaType.APPLICATION_JSON)
					.body(body)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientException e) {
			log.error("PATCH /equipment/{}/status failed: {}", equipmentId, e.getMessage());
			throw e;
		}
	}

	public void patchEquipment(Long equipmentId, UpdateEquipmentPatchRequest patch, String authorizationHeader) {
		try {
			restClient.patch()
					.uri("/equipment/{id}", equipmentId)
					.header("Authorization", authorizationHeader)
					.contentType(MediaType.APPLICATION_JSON)
					.body(patch)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientException e) {
			log.error("PATCH /equipment/{} failed: {}", equipmentId, e.getMessage());
			throw e;
		}
	}
}
