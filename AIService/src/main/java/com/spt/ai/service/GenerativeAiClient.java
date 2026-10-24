package com.spt.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class GenerativeAiClient {

    private final RestClient restClient;
    private final boolean enabled;
    private final String apiKey;
    private final String model;

    public GenerativeAiClient(
            RestClient.Builder builder,
            @Value("${ai.provider.endpoint}") String endpoint,
            @Value("${ai.provider.enabled:false}") boolean enabled,
            @Value("${ai.provider.api-key:}") String apiKey,
            @Value("${ai.provider.model:gpt-4o-mini}") String model) {
        this.restClient = builder.baseUrl(endpoint).build();
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.model = model;
    }

    public Optional<String> generate(String prompt) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }

        Map<String, Object> request = Map.of(
                "model", model,
                "temperature", 0.6,
                "max_tokens", 120,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are a concise, supportive productivity coach. Never shame the user. Return plain text only."),
                        Map.of("role", "user", "content", prompt)));
        try {
            JsonNode response = restClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);
            if (response == null) {
                return Optional.empty();
            }
            String content = response.path("choices").path(0).path("message").path("content").asText("").trim();
            return content.isBlank() ? Optional.empty() : Optional.of(content);
        } catch (RuntimeException exception) {
            log.warn("AI provider unavailable; using metric-based suggestion: {}", exception.getMessage());
            return Optional.empty();
        }
    }
}
