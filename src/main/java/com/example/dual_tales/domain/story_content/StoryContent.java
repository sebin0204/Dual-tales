package com.example.dual_tales.domain.story_content;

import com.example.dual_tales.domain.story.Story;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    private int sequence; //페이지 번호

    @Column(columnDefinition = "TEXT")
    private String question_ko;

    @Column(columnDefinition = "TEXT")
    private String question_foreign;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String content_ko; //한국어 문장

    @Column(columnDefinition = "TEXT")
    private String content_foreign; //외국어 문장

    private String image_url; //삽화 경로
}
