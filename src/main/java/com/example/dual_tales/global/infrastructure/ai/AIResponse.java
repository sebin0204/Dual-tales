package com.example.dual_tales.global.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    @JsonProperty("question_ko")
    private String questionKo;
    @JsonProperty("question_foreign")
    private String questionForeign;
    @JsonProperty("is_final")
    private boolean isFinal;
    @JsonProperty("story_state")
    private Object storyState;
}