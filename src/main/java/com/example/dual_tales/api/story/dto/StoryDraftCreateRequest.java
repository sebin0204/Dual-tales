package com.example.dual_tales.api.story.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoryDraftCreateRequest {

    @JsonProperty("request_type")
    private final String requestType = "QUESTION";

    @JsonProperty("target_lang_code")
    private String targetLangCode;

    @JsonProperty("target_age")
    private int targetAge;
}