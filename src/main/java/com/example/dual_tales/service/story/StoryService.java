package com.example.dual_tales.service.story;

import com.example.dual_tales.api.service.dto.StoryResponseDto;
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

    @Transactional(readOnly = true)
    public List<StoryResponseDto> getPublicStoriesByLanguage(String langCode) {
        //공개된 동화 중 특정 언어 최신순으로 가져와서 DTO 반환
        return storyRepository.findAllByTargetLangCodeAndIsPublicTrue(langCode)
                .stream()
                .map(StoryResponseDto::new)
                .collect(Collectors.toList());
    }
}
