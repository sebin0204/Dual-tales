package com.example.dual_tales.service.story;

import com.example.dual_tales.global.infrastructure.ai.AIResponse;
import com.example.dual_tales.global.infrastructure.ai.FinalStoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    @Override
    public AIResponse generateFirstQuestion(String langCode, int age) {
        return new AIResponse("어떤 동물을 주인공으로 할까요?", "Which animal do you want?", false);
    }

    @Override
    public AIResponse getNextQuestion(String history, String langCode) {
        // 임시로 false 반환 (테스트 시 최종 단계로 가려면 true로 바꿔서 테스트해보세요!)
        return new AIResponse("그 친구의 이름은 무엇인가요?", "What is its name?", false);
    }

    @Override
    public FinalStoryResponse generateFinalStory(String fullHistory, String langCode) {
        // 최종 원고 가짜 데이터 생성
        List<FinalStoryResponse.PageContent> mockPages = List.of(
                new FinalStoryResponse.PageContent(1, "작은 물고기가 살았어요.", "A little fish lived here.", "https://example.com/image1.png"),
                new FinalStoryResponse.PageContent(2, "바다로 여행을 떠났죠.", "It went on a sea trip.", "https://example.com/image2.png")
        );
        return new FinalStoryResponse("물고기의 모험", "https://example.com/cover.png", langCode, 6, 2, mockPages);
    }
}