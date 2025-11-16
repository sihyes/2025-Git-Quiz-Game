package kr.ac.ewha.java2.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DB에 저장되는 유저 정보 (JPA Entity)
 * - 'User'는 DB 예약어일 수 있으므로 'AppUser'로 변경
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "app_user") // 테이블 이름 변경
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 PK (Long)

    @Column(unique = true, nullable = false)
    private String username; // 로그인 ID

    @Column(nullable = false)
    private String password; // 비밀번호 (암호화 필요)

    @Column(unique = true, nullable = false)
    private String nickname; // 게임 내 닉네임

    @Column(nullable = false)
    private int score = 0; // 누적 점수 (명예의 전당용)

    public AppUser(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.score = 0; // 신규 가입 시 0점
    }
}