package com.example.dual_tales.api.user;

import com.example.dual_tales.api.user.dto.UserRequestDto;
import com.example.dual_tales.domain.user.User;
import com.example.dual_tales.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "신규 유저 등록", description = "이메일과 닉네임을 받아 새로운 유저를 생성합니다.")
    public User saveUser(@RequestBody UserRequestDto requestDto) {
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setNickname(requestDto.getNickname());

        return userRepository.save(user);
    }
}
