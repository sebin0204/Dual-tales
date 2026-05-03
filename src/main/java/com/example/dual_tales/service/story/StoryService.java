package com.example.dual_tales.service.story;

import com.example.dual_tales.api.story.dto.StoryDetailResponseDto;
import com.example.dual_tales.api.story.dto.StoryResponseDto;
import com.example.dual_tales.domain.story.Story;
import com.example.dual_tales.domain.story.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //자동 생성자 주입 어노테이션 (의존성 주입 중 하나)
public class StoryService {
    private final StoryRepository storyRepository;

    //내 동화 목록 조회
    @Transactional(readOnly = true)
    public List<StoryResponseDto> getMyStories(Long userId) {
        return storyRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(StoryResponseDto::new)
                .collect(Collectors.toList());
    }

    //동화 상세 조회
    @Transactional(readOnly = true)
    public StoryDetailResponseDto getStoryDetail(Long storyId) {
        //1. 동화 기본 정보 조회
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동화를 찾을 수 없습니다."));
        //2. 동화의 페이지 내용들을 순서대로(sequence) 조회
        return new StoryDetailResponseDto(story, story.getContent());
    }

    @Transactional(readOnly = true)
    public List<StoryResponseDto> getPublicStoriesByLanguage(String langCode) {
        //공개된 동화 중 특정 언어 최신순으로 가져와서 DTO 반환
        return storyRepository.findAllByTargetLangCodeAndIsPublicTrue(langCode)
                .stream()
                .map(StoryResponseDto::new)
                .collect(Collectors.toList());
    }
}
