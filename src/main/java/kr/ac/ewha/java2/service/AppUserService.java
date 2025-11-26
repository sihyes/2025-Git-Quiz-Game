package kr.ac.ewha.java2.service;

import kr.ac.ewha.java2.domain.entity.AppUser;
import kr.ac.ewha.java2.domain.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 생성자 주입
    public AppUserService(AppUserRepository appUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 로직
     */
    public String registerUser(String username, String password, String nickname) {
        // 1. ID 중복 검사
        if (appUserRepository.findByUsername(username).isPresent()) {
            return "FAIL: 이미 존재하는 ID입니다.";
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 유저 엔티티 생성 및 저장
        AppUser newUser = new AppUser(username, encodedPassword, nickname);
        appUserRepository.save(newUser);

        return "SUCCESS";
    }

    /**
     * 로그인 로직 (인증)
     * 성공 시 AppUser 객체 반환, 실패 시 null 반환
     */
    public AppUser authenticate(String username, String password) {
        // 1. ID로 유저 조회
        Optional<AppUser> userOptional = appUserRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return null; // 유저 없음
        }

        AppUser user = userOptional.get();

        // 2. 비밀번호 일치 여부 확인
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user; // 인증 성공
        } else {
            return null; // 비밀번호 불일치
        }
    }
    
    /* 로그인 시 본인 등수 계산합니다. */
    public long getMyRank(AppUser user) {
    	long rank = appUserRepository.calculateMyRank(user.getScore());
    	return rank;
    }
}