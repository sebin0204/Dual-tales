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
import com.fasterxml.jackson.databind.ObjectMapper; // 👈 정확히 임포트 되었는지 확인!
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryDraftService {
    private final StoryDraftRepository storyDraftRepository;
    private final AiService aiService;
    private final StoryService storyService;

    private ObjectMapper objectMapper = new ObjectMapper(); // 👈 롬복 @RequiredArgsConstructor에 의해 자동 주입됩니다.

    //1. 동화 제작 시작(최초 생성)
    public StoryDraftResponseDto createDraft(User user, StoryDraftCreateRequest dto) {
        // AI 서버에 보낼 첫 요청 조립
        Map<String, Object> aiRequest = new HashMap<>();
        aiRequest.put("request_type", "QUESTION");
        aiRequest.put("target_lang_code", dto.getTargetLangCode());
        aiRequest.put("story_lang_code", "KO");
        aiRequest.put("target_age", dto.getTargetAge());
        aiRequest.put("history", "");
        aiRequest.put("user_answer", null);
        aiRequest.put("story_state", null);

        AIResponse aiResponse = aiService.generateFirstQuestion(aiRequest);

        // story_state를 DB에 직렬화(String)해서 저장
        String initialStateStr = null;
        try {
            if (aiResponse.getStoryState() != null) {
                initialStateStr = objectMapper.writeValueAsString(aiResponse.getStoryState());
            }
        } catch (Exception e) {
            throw new RuntimeException("story_state 파싱 실패", e);
        }

        StoryDraft draft = StoryDraft.builder()
                .user(user)
                .currentStep(1)
                .targetLangCode(dto.getTargetLangCode())
                .targetAge(dto.getTargetAge())
                .history("Q1(KO): " + aiResponse.getQuestionKo() + "\nQ1(FOR): " + aiResponse.getQuestionForeign())
                .storyState(initialStateStr)
                .build();

        StoryDraft savedDraft = storyDraftRepository.save(draft);

        return StoryDraftResponseDto.from(savedDraft, aiResponse.getQuestionKo(), aiResponse.getQuestionForeign(), false);
    }

    //2. 답변 전송 및 다음 질문 받기
    public StoryDraftResponseDto proceedDraft(User user, Long draftId, String userAnswer) {
        StoryDraft draft = storyDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 드래프트를 찾을 수 없습니다."));

        // 1) 기존 히스토리에 사용자 답변 추가
        String currentHistory = draft.getHistory() + "\nA: " + userAnswer;

        // 2) DB에 문자열로 보관 중이던 storyState를 AI 전송용 Map으로 역직렬화
        Map<String, Object> savedStateMap = null;
        try {
            if (draft.getStoryState() != null) {
                savedStateMap = objectMapper.readValue(draft.getStoryState(), Map.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("story_state 복원 실패", e);
        }

        // 3) AI 담당자 규격(AiRequest)에 맞춰 요청 바디 Map 조립 ✨ (여기서 선언!)
        Map<String, Object> aiRequest = new HashMap<>();
        aiRequest.put("request_type", "QUESTION");
        aiRequest.put("target_lang_code", draft.getTargetLangCode());
        aiRequest.put("story_lang_code", "KO");
        aiRequest.put("target_age", draft.getTargetAge());
        aiRequest.put("history", currentHistory);
        aiRequest.put("user_answer", userAnswer);
        aiRequest.put("story_state", savedStateMap);

        // AI한테 지금까지의 히스토리를 보내서 다음 질문 요청
        AIResponse aiResponse = aiService.getNextQuestion(aiRequest);

        // 4) 만약 최종 완성 단계라면 (isFinal == true)
        if (aiResponse.isFinal()) {
            // request_type을 STORY로 변경하고, 마지막으로 업데이트된 최신 상태를 세팅
            aiRequest.put("request_type", "STORY");
            aiRequest.put("story_state", aiResponse.getStoryState());

            // AI에게 최종 완성본(StoryResponse) 요청
            FinalStoryResponse finalStory = aiService.generateFinalStory(aiRequest);

            // DTO 매핑 가공
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

            // DB 진짜 저장 후 임시 드래프트 삭제
            storyService.createStory(user, createDto);
            storyDraftRepository.delete(draft);

            return StoryDraftResponseDto.from(draft, "동화가 성공적으로 완성되었습니다", "The story has been successfully completed!", true);
        }

        // 5) 아직 대화가 끝나지 않은 경우 (isFinal == false)
        // 변수 유효 범위 문제 해결: 이 블록 안에서 안전하게 선언하고 사용
        int nextStep = draft.getCurrentStep() + 1;
        String updateHistory = currentHistory + "\nQ" + nextStep + "(KO): " + aiResponse.getQuestionKo();

        String nextStateStr = null;
        try {
            if (aiResponse.getStoryState() != null) {
                nextStateStr = objectMapper.writeValueAsString(aiResponse.getStoryState());
            }
        } catch (Exception e) {
            throw new RuntimeException("story_state 갱신 실패", e);
        }

        // DB 스텝 및 상태 갱신
        draft.updateStep(nextStep, updateHistory, nextStateStr);

        return StoryDraftResponseDto.from(draft, aiResponse.getQuestionKo(), aiResponse.getQuestionForeign(), false);
    }

    //3. 최종 동화 저장시 무결성 검사 및 드래프트 삭제
    public void finalizeStory(Long draftId, int pageCount, int contentSize) {
        if (pageCount != contentSize) {
            throw new IllegalArgumentException("설정된 페이지 수와 실제 페이지 데이터 개수가 일치하지 않습니다.");
        }

        storyDraftRepository.deleteById(draftId);
    }
}