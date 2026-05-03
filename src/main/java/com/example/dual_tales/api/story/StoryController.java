package com.example.dual_tales.api.story;

import com.example.dual_tales.api.story.dto.StoryDetailResponseDto;
import com.example.dual_tales.api.story.dto.StoryResponseDto;
import com.example.dual_tales.service.story.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //외부 요청을 받아서 JSON으로 응답하는 컨트롤러 어노테이션(@Controller + @ResponseBody)
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Tag(name="Story", description = "동화 관련 API")
public class StoryController {
    private final StoryService storyService;

    //GET: 내 동화 목록 조회
    @GetMapping("/my")
    @Operation(summary = "내 동화 목록 조회", description = "내가 작성한 동화 리스트를 최신순으로 조회합니다.")
    public List<StoryResponseDto> getMyStories() {
        //테스트를 위해 임시로 1번 유저 데이터 가져오게 설정
        Long tempUserId = 1L;
        return storyService.getMyStories(tempUserId);
    }

    //GET: 동화 상세 조회
    @GetMapping("/{storyId}")
    @Operation(summary = "동화 상세 조회", description = "동화의 제목과 모든 페이지 내용을 순서대로 반환합니다.")
    public StoryDetailResponseDto getStoryDetail(@PathVariable Long storyId) {
        return storyService.getStoryDetail(storyId);
    }
    //GET: 언어별 공유 피드 조회
    @GetMapping("/feed")
    @Operation(summary = "언어별 공유 피드 조회", description = "언어코드(KO, EN, FR)를 받아 해당 언어의 공개 동화 목록을 반환")
    public List<StoryResponseDto> getFeedByLanguage(@RequestParam String langCode){
        return storyService.getPublicStoriesByLanguage(langCode);
    }
}