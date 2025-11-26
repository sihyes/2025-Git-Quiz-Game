package kr.ac.ewha.java2.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.ac.ewha.java2.domain.entity.Question;

import java.util.List;

/**
 * Question Entity용 JPA Repository
 * - 담당: 원용
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * DB에서 랜덤으로 문제를 N개 가져옵니다.
     * (MySQL/MariaDB 기준의 nativeQuery)
     */
    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("count") int count);
}