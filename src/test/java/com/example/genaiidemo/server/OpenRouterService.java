package com.example.genaiidemo.server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service("openRouterService")
public class OpenRouterService implements QuestionGeneratorService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    @Override
    public String generateQuestion(String topic, String difficulty) {
        String prompt = String.format("Generate a %s difficulty exam question on the topic: %s.", difficulty, topic);
        return callOpenRouter(prompt);
    }

    @Override
    public String generateQuestionFromText(String text, String markType) {
        String prompt = String.format("From the following content, generate a %s exam question:\n\n%s", markType, text);
        return callOpenRouter(prompt);
    }

    private String callOpenRouter(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "mistralai/mistral-7b-instruct:free");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are an expert exam question generator."));
        messages.add(Map.of("role", "user", "content", prompt));
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
            JSONObject obj = new JSONObject(response.getBody());
            JSONArray choices = obj.getJSONArray("choices");
            return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            System.err.println("❌ OpenRouter HTTP Error:");
            System.err.println("Status Code: " + ex.getStatusCode());
            System.err.println("Response Body: " + ex.getResponseBodyAsString());

            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return "[OpenRouter Error] Rate limit hit. Please try again in a minute.";
            }

            return "[OpenRouter Error] HTTP " + ex.getStatusCode();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error from OpenRouter:");
            e.printStackTrace();
            return "[OpenRouter Error] Unexpected failure. Try again.";
        }
    }
}
