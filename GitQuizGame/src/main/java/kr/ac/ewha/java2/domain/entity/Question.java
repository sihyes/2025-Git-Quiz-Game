package kr.ac.ewha.java2.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DB에 저장되는 문제 뱅크 (JPA Entity)
 * - 담당: 원용
 */
@Entity
@Getter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String questionText; // 퀴즈 문제

    @Column(nullable = false)
    private String answer; // 정답

    @Column(nullable = false)
    private int score; // 해당 문제의 배점
}
