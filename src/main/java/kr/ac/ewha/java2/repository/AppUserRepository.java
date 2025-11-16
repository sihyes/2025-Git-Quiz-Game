package kr.ac.ewha.java2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.ac.ewha.java2.domain.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // 로그인 시 사용할 메서드
    Optional<AppUser> findByUsername(String username);

    // 닉네임 중복 체크 시 사용할 메서드
    boolean existsByNickname(String nickname);
}