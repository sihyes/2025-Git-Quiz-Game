package kr.ac.ewha.java2.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubmitAnswerRequestDto {
    //사용자 식별 ID
    private Long participantId;
    //현재 문제의 Id
    private Integer questionId;
    //사용자가 제출한 정답 내용
    private String submittedAnswer;

    public SubmitAnswerRequestDto(){}
    public SubmitAnswerRequestDto(Long participantId, Integer questionId, String submittedAnswer){
        this.participantId = participantId;
        this.questionId =  questionId;
        this.submittedAnswer = submittedAnswer;
    }
}
