package com.example.dual_tales.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//extends JpaRepository<User,Long> : 유저 데이터를 관리하는 인터페이스임을 명시(유저 저장, 찾기, 가져오기 삭제 함수를 자동으로 만들어줌)
public interface UserRepository extends JpaRepository<User, Long> {
    //닉네임으로 유저를 찾는 기능
    Optional<User> findByNickname(String nickname);
}
