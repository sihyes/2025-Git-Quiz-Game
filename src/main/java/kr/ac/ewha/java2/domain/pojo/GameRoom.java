package kr.ac.ewha.java2.domain.pojo;



import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.ewha.java2.domain.entity.Question;


@Getter
@Setter
public class GameRoom {

    private Long roomId; // 방 고유 ID
    private String roomName; // 방 제목
    private Long hostId; // 방장 User ID
    private String hostNickname; // 방장 닉네임

    // ---- 통합된 방 설정 ----
    private int questionCount; // 총 문제 수
    private int timeLimitPerQuestion; // 문제당 제한 시간 (초)
    private int maxParticipants; // 최대 인원 (예: 8)
    // ---------------------

    // Key: userId (Long)
    private Map<Long, Participant> participants = new HashMap<>();

    private GameState state; // 현재 방 상태 (WAITING, PLAYING, FINISHED)

    private List<Question> questions; // 이번 게임에서 사용할 문제 목록
    private int currentQuestionIndex; // 현재 문제 인덱스

    public GameRoom(Long roomId, String roomName, Long hostId, String hostNickname, int questionCount, int timeLimit, int maxParticipants) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.hostId = hostId;
        this.hostNickname = hostNickname;
        this.questionCount = questionCount;
        this.timeLimitPerQuestion = timeLimit;
        this.maxParticipants = maxParticipants;
        this.state = GameState.WAITING;
        this.currentQuestionIndex = 0;
    }
    // --- 게임 진행에 필요한 편의 메서드들 ---

    public void addParticipant(Participant participant) {
        participants.put(participant.getUserId(), participant);
    }

    public void removeParticipant(Long userId) {
        participants.remove(userId);
    }

    public Participant getParticipant(Long userId) {
        return participants.get(userId);
    }
    
    public int getCurrentParticipantCount() {
        return participants.size();
    }
    
    public Question getCurrentQuestion() {
        if (questions == null || currentQuestionIndex >= questions.size()) {
            return null; // 문제가 없거나 끝남
        }
        return questions.get(currentQuestionIndex);
    }
}
