package com.invtr.equipmentservice.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invtr.equipmentservice.dto.ConditionAssessmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ConditionAssessmentResponse assessCondition(MultipartFile file) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "You are an equipment condition assessor. Look at this image and assess the condition of the equipment. Respond in this exact JSON format with no extra text: {\\"condition\\": \\"EXCELLENT|GOOD|DAMAGED|BROKEN\\", \\"reasoning\\": \\"brief explanation of what you see\\"}"
                        },
                        {
                          "inline_data": {
                            "mime_type": "%s",
                            "data": "%s"
                          }
                        }
                      ]
                    }
                  ]
                }
                """.formatted(mimeType, base64Image);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Gemini API error: {}", response.body());
                throw new RuntimeException("Gemini API returned status: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String text = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            JsonNode result = objectMapper.readTree(text.trim());
            String condition = result.path("condition").asText();
            String reasoning = result.path("reasoning").asText();

            return new ConditionAssessmentResponse(condition, reasoning);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gemini API call interrupted");
        }
    }
}