package com.example.dual_tales.global.infrastructure.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiAiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.api.key}")
    private String apiKey;

    private final String AI_SERVER_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public ResponseEntity<String> callAiServer(Map<String, Object> requestBody) {
        // 1. 헤더 설정 (제미나이는 Authorization Bearer 헤더 대신 URL 파라미터로 키를 받으므로 content-type만 설정합니다)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. 요청 본문과 헤더 합치기
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String finalUrl = AI_SERVER_URL + "?key=" + apiKey;

        // 4. 완성된 절대 주소(finalUrl)로 AI 서버에 POST 요청 날리기!
        return restTemplate.exchange(
                finalUrl,
                HttpMethod.POST,
                entity,
                String.class
        );
    }
}