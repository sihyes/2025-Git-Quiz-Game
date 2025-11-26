package kr.ac.ewha.java2.controller;

import jakarta.servlet.http.HttpSession;
import kr.ac.ewha.java2.domain.entity.AppUser;
import kr.ac.ewha.java2.domain.repository.AppUserRepository;
import kr.ac.ewha.java2.service.AppUserService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppUserController {

    private final AppUserRepository appUserRepository;

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService, AppUserRepository appUserRepository) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
    }

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    public String signup(@RequestBody Map<String, String> params) {
        String id = params.get("id");
        String pw = params.get("pw");
        String nickname = params.get("nickname");

        // Service에게 회원가입 처리 위임
        return appUserService.registerUser(id, pw, nickname);
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> params, HttpSession session) {
    	System.out.println("========== [로그인 요청 도착] ==========");
        System.out.println("받은 데이터: " + params);
    	String id = params.get("id");
        String pw = params.get("pw");

        // Service에게 인증 요청
        AppUser user = appUserService.authenticate(id, pw);

        if (user != null) {
            // 로그인 성공: 세션에 유저 정보 저장
            session.setAttribute("user", user);
            return "SUCCESS:" + user.getNickname();
        } else {
            // 로그인 실패
        	System.out.println("❌ 로그인 실패: 아이디 없음 또는 비번 틀림");
            return "FAIL: 아이디 또는 비밀번호가 잘못되었습니다.";
        }
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 날리기
        return "SUCCESS";
    }

    /**
     * 현재 로그인한 사용자 정보 조회 (로비 화면용)
     */
    @GetMapping("/me")
    public AppUser getMyInfo(HttpSession session) {
        // 세션에서 저장된 user 객체를 꺼내서 반환 (없으면 null)
        return (AppUser) session.getAttribute("user");
    }
    
    @GetMapping("/myrank")
    public long getMyRank(HttpSession session) {
    	// 1. 세션에서 유저 객체 가져오기 (Object 타입)
        Object sessionUser = session.getAttribute("user");

        // 2. 로그인 여부 체크 (세션에 값이 없거나 타입이 안 맞으면 -1 반환)
        if (sessionUser == null || !(sessionUser instanceof AppUser)) {
            return -1; // -1은 "로그인 안 됨"을 의미하는 약속된 값
        }
        AppUser user = (AppUser) sessionUser;   
        
    	return appUserService.getMyRank(user);
    	
    }
}