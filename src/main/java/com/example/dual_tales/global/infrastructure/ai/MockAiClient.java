package com.example.dual_tales.global.infrastructure.ai;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

//AI 연결 전 연동 테스트용 !

@Component
@Primary
public class MockAiClient implements AiClient {

    @Override
    public AIResponse getNextQuestion(String history, String targetLangCode, int targetAge){
        return new AIResponse(
                "주인공 토끼의 이름은 무엇인가요?",
                "What is the name of the main character rabbit?",
                false
        );
    }

    @Override
    public FinalStoryResponse generateFinalStory(String fullHistory, String targetLangCode) {
        return new FinalStoryResponse("행복한 토끼 이야기", 5, List.of(...));
    }

}
