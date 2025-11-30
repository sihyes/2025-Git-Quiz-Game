package kr.ac.ewha.java2.domain.repository;

import kr.ac.ewha.java2.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // 로그인 시 사용할 메서드
    Optional<AppUser> findByUsername(String username);

    // 닉네임 중복 체크 시 사용할 메서드
    boolean existsByNickname(String nickname);
    // 랭킹조회 메서드 - 1. 전체 유저 중 10명 조회하기
    List<AppUser> findTop10ByOrderByScoreDesc();
    // 랭킹조회메서드 - 2. 내 등수 계산(내 점수보다 높은사람의수 +1)
    @Query("SELECT COUNT(u) + 1 FROM AppUser u WHERE u.score > :score")
    long calculateMyRank(@Param("score") int score);
    //점수 업데이트...
    @Modifying
    @Transactional
    @Query("UPDATE AppUser u SET u.score = :newScore WHERE u.id = :userId")
    void updateParticipantScore(@Param("userId") Long userId, @Param("newScore") int newScore);
}