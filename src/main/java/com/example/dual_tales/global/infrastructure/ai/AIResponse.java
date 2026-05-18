package com.example.dual_tales.global.infrastructure.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
    private String questionKo;
    private String questionForeign;
    private boolean isFinal;
}
