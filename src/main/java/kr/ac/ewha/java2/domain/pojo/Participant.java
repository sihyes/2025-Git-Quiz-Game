package kr.ac.ewha.java2.domain.pojo;


import kr.ac.ewha.java2.domain.entity.AppUser;
import lombok.Getter;
import lombok.Setter;
/**
 * 게임 방에 참여한 유저 정보 (In-Memory POJO)
 * - 담당: 원용
 */
@Getter
@Setter
public class Participant {

    private Long userId;    // User의 PK
    private String nickname; // User의 닉네임 (게임 내 표시용)
    private int score;      // 이번 게임의 실시간 점수

    public Participant(AppUser user) {
        this.userId = user.getId(); //123456765434567545
        this.nickname = user.getNickname();
        this.score = 0; // 게임 시작 시 0점
    }
}