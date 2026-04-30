package com.example.dual_tales.api.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoryRequestDto {
    private String title;
    private String targetLangCode;
    private String targetAge;
    private boolean isPublic;
}
