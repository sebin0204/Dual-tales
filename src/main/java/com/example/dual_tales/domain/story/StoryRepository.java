package com.example.dual_tales.domain.story;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findAllByIsPublicTrueOrderByCreatedAtDesc(); //공개된 동화책 최신순 조회
    List<Story> findAllByTargetAgeAndIsPublicTrue(int targetAge);
    List<Story> findByUser_Id(Long userId); //특정 유저가 만든 스토리 목록 가져오기

}
