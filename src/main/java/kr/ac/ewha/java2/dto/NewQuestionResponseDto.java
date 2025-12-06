package kr.ac.ewha.java2.dto;

import lombok.Getter;

//새로운 퀴즈 질문 정보를 사용자에게 전달
//서버 -> 클라이언트
@Getter
public class NewQuestionResponseDto {
    //문제 ID
    private Long questionId;
    //문제 내용
    private String questionText;
    //제한 시간
    private int timeLimit;

    //type
    private final String type = "QUESTION";
    public NewQuestionResponseDto(Long questionId, String questionText, int timeLimit){
        this.questionId =  questionId;
        this.questionText = questionText;
        this.timeLimit = timeLimit;
    }
}
