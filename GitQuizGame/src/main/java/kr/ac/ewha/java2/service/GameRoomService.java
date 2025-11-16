package kr.ac.ewha.java2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import kr.ac.ewha.java2.domain.entity.AppUser;
import kr.ac.ewha.java2.domain.entity.Question;
import kr.ac.ewha.java2.domain.pojo.GameRoom;
import kr.ac.ewha.java2.domain.pojo.Participant;
import kr.ac.ewha.java2.dto.CreateRoomRequestDto;
import kr.ac.ewha.java2.repository.AppUserRepository;
import kr.ac.ewha.java2.repository.QuestionRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 싱글톤 서비스 (게임 매니저)
 * 모든 실시간 게임 방(In-Memory)을 관리합니다.
 * - 담당: 시은 (핵심 로직), 원용 (DB 연동)
 */
@Service
@RequiredArgsConstructor
public class GameRoomService {

    // (서버 메모리 역할) Key: roomId (Long)
    private final Map<Long, GameRoom> activeRooms = new ConcurrentHashMap<>();
    private final AtomicLong roomIdCounter = new AtomicLong(1L); // 방 ID 생성기

    // (DB 연동)
    private final QuestionRepository questionRepository;
    private final AppUserRepository userRepository;

    // (WebSocket 메시징용)
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * (Spec) 새 방을 생성 및 activeRooms에 추가
     */
    public GameRoom createRoom(CreateRoomRequestDto dto, AppUser host) {
        long newRoomId = roomIdCounter.getAndIncrement();

        // 1. DB에서 요청한 개수만큼 랜덤 문제 가져오기
        List<Question> questions = questionRepository.findRandomQuestions(dto.getQuestionCount());

        // 2. 새 게임 방 생성
        GameRoom newRoom = new GameRoom(
                newRoomId,
                dto.getRoomName(),
                host.getId(),
                dto.getQuestionCount(),
                dto.getTimeLimitPerQuestion()
        );
        newRoom.setQuestions(questions); // 문제 목록 설정

        // 3. 방장(Host)을 참여자로 추가
        Participant hostParticipant = new Participant(host);
        newRoom.addParticipant(hostParticipant);

        // 4. 메모리에 방 추가
        activeRooms.put(newRoomId, newRoom);
        return newRoom;
    }

    /**
     * (Spec) ID로 방 찾기
     */
    public GameRoom findRoomById(Long roomId) {
        return activeRooms.get(roomId);
    }

    /**
     * (Spec) 방 삭제 (게임 종료 시)
     */
    public void removeRoom(Long roomId) {
        activeRooms.remove(roomId);
        // TODO: 방이 삭제되었다고 로비에 브로드캐스트
    }

    /**
     * (Spec) 로비에 보여줄 방 목록 반환
     */
    public Collection<GameRoom> getAllRooms() {
        return activeRooms.values();
    }

    // --- 게임 로직 메서드 (시은 담당) ---

    public void joinRoom(Long roomId, AppUser user) {
        GameRoom room = findRoomById(roomId);
        if (room != null) {
            // TODO: 방이 꽉 찼는지, 게임 중인지 체크
            Participant newParticipant = new Participant(user);
            room.addParticipant(newParticipant);

            // TODO: WebSocket으로 방의 모든 유저에게 새 참여자 정보 브로드캐스트
            // messagingTemplate.convertAndSend("/topic/room/" + roomId, ...);
        }
    }

    public void processAnswer(Long roomId, Long userId, String answer) {
        // TODO: 정답 확인, 점수 처리, 다음 문제 전송 로직
    }

    public void finishGame(Long roomId) {
        GameRoom room = findRoomById(roomId);
        if (room == null) return;

        // TODO: 게임 종료 처리 및 WebSocket 브로드캐스트

        // (명예의 전당) 참여자들의 점수를 DB에 누적
        for (Participant p : room.getParticipants().values()) {
            userRepository.findById(p.getUserId()).ifPresent(user -> {
                user.setScore(user.getScore() + p.getScore());
                userRepository.save(user);
            });
        }
    }
}