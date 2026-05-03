package com.example.dual_tales.api.story_content.dto;

import com.example.dual_tales.domain.story_content.StoryContent;
import lombok.Getter;

@Getter
public class StoryContentResponseDto {
    private int sequence;
    private String question;
    private String answer;
    private String content_Ko;
    private String content_foreign;
    private String image_url;

    public StoryContentResponseDto(StoryContent content) {
        this.sequence = content.getSequence();
        this.question = content.getQuestion();
        this.answer = content.getAnswer();
        this.content_Ko = content.getContent_ko();
        this.content_foreign = content.getContent_foreign();
        this.image_url = content.getImage_url();
    }
}
