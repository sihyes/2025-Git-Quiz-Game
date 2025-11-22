package kr.ac.ewha.java2.dto;

import lombok.Getter;

//새로운 퀴즈 질문 정보를 사용자에게 전달
//서버 -> 클라이언트
@Getter
public class NewQuestionResponseDto {
    //문제 ID
    private Integer questionId;
    //문제 내용
    private String questionText;
    public NewQuestionResponseDto(){}
    public NewQuestionResponseDto(Integer questionId, String questionText){
        this.questionId =  questionId;
        this.questionText = questionText;
    }
}
