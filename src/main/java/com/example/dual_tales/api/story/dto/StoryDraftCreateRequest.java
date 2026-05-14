package com.example.dual_tales.api.story.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoryDraftCreateRequest {
    private String targetLangCode;
    private int targetAge;
}
