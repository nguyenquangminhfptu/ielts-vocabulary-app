package org.devnqminh.ieltsvoca.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.devnqminh.ieltsvoca.entity.WordEntry;
import org.devnqminh.ieltsvoca.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiAIService implements AIService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    @Override
    public WordEntry generateWordInfo(String word) {
        if (apiKey == null || apiKey.isEmpty()) {
            return generateMockData(word);
        }

        try {
            String prompt = String.format(
                    "Explain the word '%s' for an IELTS learner. " +
                            "Return ONLY a raw JSON object (no markdown formatting) with the following fields: " +
                            "word (string), definition (string), exampleSentences (array of strings), " +
                            "synonyms (array of strings), antonyms (array of strings), " +
                            "ieltsUsage (string, e.g. 'Band 7+'), band7SampleSentence (string).",
                    word
            );

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", Collections.singletonList(part));
            requestBody.put("contents", Collections.singletonList(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_URL + apiKey, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseGeminiResponse(response.getBody(), word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return generateMockData(word);
    }

    private WordEntry parseGeminiResponse(String responseBody, String originalWord) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Clean up markdown code blocks if present
            text = text.replace("```json", "").replace("```", "").trim();

            JsonNode json = objectMapper.readTree(text);

            return WordEntry.builder()
                    .word(json.path("word").asText(originalWord))
                    .definition(json.path("definition").asText())
                    .exampleSentencesJson(json.path("exampleSentences").toString())
                    .synonymsJson(json.path("synonyms").toString())
                    .antonymsJson(json.path("antonyms").toString())
                    .ieltsUsage(json.path("ieltsUsage").asText())
                    .band7SampleSentence(json.path("band7SampleSentence").asText())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return generateMockData(originalWord);
        }
    }

    private WordEntry generateMockData(String word) {
        // Fallback mock data
        return WordEntry.builder()
                .word(word)
                .definition("This is a mock definition for " + word + ". Please configure a valid Gemini API Key.")
                .exampleSentencesJson("[\"Example sentence 1 for " + word + "\", \"Example sentence 2\"]")
                .synonymsJson("[\"synonym1\", \"synonym2\"]")
                .antonymsJson("[\"antonym1\"]")
                .ieltsUsage("Band 6.0")
                .band7SampleSentence("Complex sentence using " + word + ".")
                .build();
    }
}
