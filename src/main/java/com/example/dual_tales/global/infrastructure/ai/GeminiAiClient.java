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

    // AI 담당자 팀원분의 진짜 파이썬 서버 API 엔드포인트 주소
    private final String AI_SERVER_URL = "https://dual-tales-service-771416002545.asia-northeast3.run.app/v1/ai/generate";

    public ResponseEntity<String> callAiServer(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 2. 요청 본문과 헤더 합치기
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                AI_SERVER_URL,
                HttpMethod.POST,
                entity,
                String.class
        );
    }
}