package com.example.dual_tales.domain.story;

import com.example.dual_tales.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 개의 Story가 한명의 유저에게 속함(N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //외래키 연결
    private User user; //JPA 방식(아이디만 가져오는것이 아니라, user 속 모든 정보를 들고있게 함)

    private String title;
    private String targetLangCode; //모국어 코드(ex: en, vi 등)
    private String status;
    private int targetAge;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isPublic = true;
}
