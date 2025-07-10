package com.example.genaiidemo.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("openAiService")
public class OpenAiService implements QuestionGeneratorService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateQuestion(String topic, String difficulty) {
        String prompt = "Generate a " + difficulty + " level exam question about the topic: \"" + topic + "\".";
        return sendPrompt(prompt);
    }

    @Override
    public String generateQuestionFromText(String content, String markType) {
        String prompt = "From the below unit content, generate a UNIQUE " + markType + "-mark exam question:\n\n" + content;
        return sendPrompt(prompt);
    }

    private String sendPrompt(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");  // Optional but useful for OpenRouter
        headers.set("X-Title", "GenAI Exam Generator");        // Optional for tracing in OpenRouter

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.7);       // Optional - controls creativity
        requestBody.put("max_tokens", 500);        // Optional - controls answer length

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> messageMap = (Map<String, Object>) choices.get(0).get("message");
                    return messageMap.get("content").toString().trim();
                }
            }
            return "[OpenRouter] No valid response received.";
        } catch (Exception e) {
            return "[OpenRouter Error] " + e.getMessage();
        }
    }
}
