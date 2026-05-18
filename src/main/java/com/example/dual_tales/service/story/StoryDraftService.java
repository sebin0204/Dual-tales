package com.example.dual_tales.service.story;

import com.example.dual_tales.api.story.dto.StoryCreateRequestDto;
import com.example.dual_tales.api.story.dto.StoryDraftCreateRequest;
import com.example.dual_tales.api.story.dto.StoryDraftResponseDto;
import com.example.dual_tales.domain.story.StoryRepository;
import com.example.dual_tales.domain.story_draft.StoryDraft;
import com.example.dual_tales.domain.story_draft.StoryDraftRepository;
import com.example.dual_tales.domain.user.User;
import com.example.dual_tales.global.infrastructure.ai.AIResponse;
import com.example.dual_tales.global.infrastructure.ai.FinalStoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryDraftService {
    private final StoryDraftRepository storyDraftRepository;
    private final AiService aiService;
    private final StoryService storyService;

    //1. 동화 제작 시작(최초 생성)
    public StoryDraftResponseDto createDraft(User user, StoryDraftCreateRequest dto) {
        AIResponse aiResponse = aiService.generateFirstQuestion(dto.getTargetLangCode(), dto.getTargetAge());

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
    public StoryDraftResponseDto proceedDraft(User user, Long draftId, String userAnswer) {
        StoryDraft draft = storyDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 드래프트를 찾을 수 없습니다."));

        //1. 기존 히스토리에 사용자 답변 추가
        String currentHistory = draft.getHistory() + "\nA: " + userAnswer;

        //2. AI한테 지금까지의 히스토리를 보내서 다음 질문 요청
        AIResponse aiResponse = aiService.getNextQuestion(currentHistory, draft.getTargetLangCode());

        //3. isFinal==true
        if(aiResponse.isFinal()) {
            // AI에게 FinalStoryResponse 요청
            FinalStoryResponse finalStory = aiService.generateFinalStory(currentHistory, draft.getTargetLangCode());
            //FinalStoryResponse 데이터를 StoryCreateRequestDto 형식으로 변환(매핑)
            List<StoryCreateRequestDto.ContentDto> contentsDto = finalStory.getPages().stream()
                    .map(page -> new StoryCreateRequestDto.ContentDto(
                            page.getSequence(),
                            page.getContentKo(),
                            page.getContentForeign(),
                            page.getImageUrl()
                    )).toList();

            StoryCreateRequestDto createDto = new StoryCreateRequestDto(
                    draft.getId(),
                    finalStory.getTitle(),
                    finalStory.getCoverImageUrl(),
                    finalStory.getTargetLangCode(),
                    finalStory.getTargetAge(),
                    finalStory.getPageCount(),
                    contentsDto
            );

            //StoryService.createStory(user,dto)를 호출해 DB에 최종 저장
            storyService.createStory(user, createDto);

            //저장에 성공하면 StoryDraft 삭제
            storyDraftRepository.delete(draft);

            //프론트에게 종료 알림
            return StoryDraftResponseDto.from(draft, "동화가 성공적으로 완성되었습니다", "The story has been successfully completed!", true);
        }

        int nextStep = draft.getCurrentStep() +1;
        String updateHistory = currentHistory + "\nQ" + nextStep + "(KO): " + aiResponse.getQuestionKo();
        draft.updateStep(nextStep, updateHistory);

        return StoryDraftResponseDto.from(draft, aiResponse.getQuestionKo(), aiResponse.getQuestionForeign(), false);
    }

    //3. 최종 동화 저장시 무결성 검사 및 드래프트 삭제
    public void finalizeStory(Long draftId, int pageCount, int contentSize) {
        if(pageCount != contentSize) {
            throw new IllegalArgumentException("설정된 페이지 수와 실제 페이지 데이터 개수가 일치하지 않습니다.");
        }

        storyDraftRepository.deleteById(draftId);
    }
}
