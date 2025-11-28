package kr.ac.ewha.java2.domain.pojo;

import kr.ac.ewha.java2.domain.entity.AppUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Participant {

    private Long userId;    // User의 PK
    private String nickname; // User의 닉네임 (게임 내 표시용)
    private int score;      // 이번 게임의 실시간 점수

    public Participant(AppUser user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.score = 0; 
    }
    	
    public Participant(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
        this.score = 0;
    }
}