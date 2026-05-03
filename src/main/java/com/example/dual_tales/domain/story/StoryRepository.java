package com.example.dual_tales.domain.story;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    //선택한 언어의 공개 동화 최신순 조회
    List<Story> findAllByTargetLangCodeAndIsPublicTrue(String targetLangCode);
    //제목에 특정 단어가 포함된 공개 동화 찾기
    List<Story> findByTitleContainingAndIsPublicTrue(String keyword);
    //공개된 동화책 최신순 조회
    List<Story> findAllByIsPublicTrueOrderByCreatedAtDesc();
    List<Story> findAllByTargetAgeAndIsPublicTrue(int targetAge);
    List<Story> findByUser_Id(Long userId); //특정 유저가 만든 스토리 목록 가져오기
    //내 동화 최신순 조회
    List<Story> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
