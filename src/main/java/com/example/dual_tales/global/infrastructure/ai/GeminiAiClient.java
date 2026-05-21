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

    private final String AI_SERVER_URL = "";

    public ResponseEntity<String> callAiServer(Map<String, Object> requestBody) {
        // 1. 헤더 설정 (보안 키 및 JSON 타입 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey); // AI 담당자가 요구하는 헤더명으로 수정 가능

        // 2. 요청 본문과 헤더 합치기
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 3. AI 서버로 POST 요청 날려서 응답 받아오기!
        return restTemplate.exchange(
                AI_SERVER_URL,
                HttpMethod.POST,
                entity,
                String.class
        );
    }

}
