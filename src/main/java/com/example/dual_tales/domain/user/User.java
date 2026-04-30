package com.example.dual_tales.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //ID 자동 생성
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true) //email null값 불허, 한 이메일 복수 계정 생성 불허
    private String email;

    private String nickname;

    @Column(nullable = false)
    private String role; //사용자 관리자 권한 (USER, ADMIN)
}
