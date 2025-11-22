package kr.ac.ewha.java2.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessageDto {
    enum MessageType{
        SYSTEM,// 시스템 메시지(입장, 정답 확인 등..?)
        MESSAGE//일반 메시지
    }

    //사용자 ID
    private Integer userId;
    //보낸 메시지
    private String message;
    //보낸 사람 이름
    private String senderName;
    //보낸 시간
    private LocalDateTime timestamp;
    //방 ID
    private Integer roomId;
    //메시지 타입
    private MessageType type;
}
