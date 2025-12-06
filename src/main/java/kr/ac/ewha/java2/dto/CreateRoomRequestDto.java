package kr.ac.ewha.java2.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequestDto {
	private String title;
	private int questionCount;
	private int timeLimitPerQuestion;
	private int maxParticipants;
}
