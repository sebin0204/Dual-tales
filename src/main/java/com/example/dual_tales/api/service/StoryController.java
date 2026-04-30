package com.example.dual_tales.api.service;

import com.example.dual_tales.api.service.dto.StoryResponseDto;
import com.example.dual_tales.service.story.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //외부 요청을 받아서 JSON으로 응답하는 컨트롤러 어노테이션(@Controller + @ResponseBody)
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Tag(name="Story", description = "동화 관련 API")
public class StoryController {
    private final StoryService storyService;

    @GetMapping("/feed")
    @Operation(summary = "언어별 공유 피드 조회", description = "언어코드(KO, EN, FR)를 받아 해당 언어의 공개 동화 목록을 반환")
    public List<StoryResponseDto> getFeedByLanguage(@RequestParam String langCode) {
        return storyService.getPublicStoriesByLanguage(langCode);
    }
}