package com.example.dual_tales.service.story;

import com.example.dual_tales.api.story.dto.StoryDraftCreateRequest;
import com.example.dual_tales.api.story.dto.StoryDraftResponseDto;
import com.example.dual_tales.domain.story.Story;
import com.example.dual_tales.domain.story.StoryRepository;
import com.example.dual_tales.domain.story_draft.StoryDraft;
import com.example.dual_tales.domain.story_draft.StoryDraftRepository;
import com.example.dual_tales.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryDraftService {
    private final StoryDraftRepository storyDraftRepository;
    private final StoryRepository storyRepository; //최종 저장 확인용
    private final AiService aiService;

    //1. 동화 제작 시작(최초 생성)
    public StoryDraftResponseDto createDraft(User user, StoryDraftCreateRequest dto) {
        AIResponse aiResponse = aiService.generateFirstQuestion(dto.getTragetLangCode(), dto.getTargetAge());

        StoryDraft draft = StoryDraft.builder()
                .user(user)
                .currentStep(1)
                .targetLangCode(dto.getTargetLangCode())
                .targetAge(dto.getTargetAge())
                .history("Q1(KO): " + aiResponse.getQuestionKo() + "\nQ1(FOR): " + aiResponse.getQuestionForeign())
                .build();

        StoryDraft savedDraft =  storyDraftRepository.save(draft);

        return StoryDraftResponseDto.from(savedDraft, aiResponse.getQuestionKo(), aiResponse.getQuestionForeign(), false);
    }

    //2. 답변 정송 및 다음 질문 받기
    public StoryDraftResponseDto proceedDraft(Long draftId, String userAnswer) {
        StoryDraft draft = storyDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 드래프트를 찾을 수 없습니다."));

        //1. 기존 히스토리에 사용자 답변 추가
        String currentHistory = draft.getHistory() + "\nA: " + userAnswer;

        //2. AI한테 지금까지의 히스토리를 보내서 다음 질문 요청
        AIResponse aiResponse = aiService.getNextQuestion(currentHistory, draft.getTargetLangCode());

        int nextStep = draft.getCurrentStep() +1;
        boolean isFinal = aiResponse.isLast();

        //3. 히스토리 업데이트
        String updateHistory = currentHistory + "\nQ" + nextStep + "(KO): " + aiResponse.getQuestionKo();
        draft.updateStep(nextStep, updateHistory);

        return StoryDraftResponseDto.from(draft, aiResponse.getQuestionKo(), aiResponse.getQuestionForeign(), isFinal);
    }

    //3. 최종 동화 저장시 무결성 검사 및 드래프트 삭제
    public void finalizeStory(Long draftId, int pageCount, int contentSize) {
        if(pageCount != contentSize) {
            throw new IllegalArgumentException("설정된 페이지 수와 실제 페이지 데이터 개수가 일치하지 않습니다.");
        }

        storyDraftRepository.deleteById(draftId);
    }
}
