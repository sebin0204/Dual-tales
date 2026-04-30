package com.example.dual_tales.api.service.dto;

import com.example.dual_tales.domain.story.Story;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StoryResponseDto {
    private Long id;
    private String title;
    private String targetLangCode;
    private int targetAge;
    private LocalDateTime createdAt;
    private String nickname;

    public StoryResponseDto(Story story) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.targetLangCode = story.getTargetLangCode();
        this.targetAge = story.getTargetAge();
        this.createdAt = story.getCreatedAt();
        this.nickname = story.getUser().getNickname();

    }
}
