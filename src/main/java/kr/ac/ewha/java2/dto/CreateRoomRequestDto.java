package kr.ac.ewha.java2.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateRoomRequestDto {
	private String roomName;
    private Integer questionCount;
	private Integer timeLimitPerQuestion;
}
