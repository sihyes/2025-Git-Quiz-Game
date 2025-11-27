package kr.ac.ewha.java2.service;

import kr.ac.ewha.java2.domain.pojo.GameRoom;
import kr.ac.ewha.java2.domain.pojo.Participant;
import kr.ac.ewha.java2.domain.repository.AppUserRepository;
import kr.ac.ewha.java2.domain.repository.QuestionRepository;
import kr.ac.ewha.java2.dto.CreateRoomRequestDto;
import kr.ac.ewha.java2.global.handler.LobbyWebSocketHandler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameRoomService {

    // 메모리에 방 저장 (Key: RoomId)
    private final Map<Long, GameRoom> activeRooms = new ConcurrentHashMap<>();
    private final AtomicLong roomIdCounter = new AtomicLong(1); // 방 번호 생성기

    private final QuestionRepository questionRepository;
    private final AppUserRepository appUserRepository;
    
    private final LobbyWebSocketHandler lobbyHandler; // 로비 갱신 알림용

    public GameRoomService(QuestionRepository questionRepository, AppUserRepository appUserRepository, LobbyWebSocketHandler lobbyHandler) {
        this.questionRepository = questionRepository;
        this.appUserRepository = appUserRepository;
        this.lobbyHandler = lobbyHandler;
    }

    /**
     * 방 생성 로직
     */
    public GameRoom createRoom(CreateRoomRequestDto request, Long hostId, String hostNickname) {
        long roomId = roomIdCounter.getAndIncrement();
        
        // 기본 설정: 문제 5개, 시간 10초
        GameRoom room = new GameRoom(
                roomId, 
                request.getTitle(), 
                hostId, 
                hostNickname,
                request.getQuestionCount(), 
                request.getTimeLimitPerQuestion(),
                request.getMaxParticipants()
        );
                
        activeRooms.put(roomId, room);
        // 로비에 있는 사람들에게 "새 방이 생겼어!" 하고 알림 (선택 사항)
        lobbyHandler.broadcastRoomList(hostNickname); 
        
        System.out.println("✅ 방 생성됨: " + room.getRoomName() + " (ID: " + roomId + ")");
        return room;
    }

    public GameRoom findRoomById(Long roomId) {
        return activeRooms.get(roomId);
    }

    public Collection<GameRoom> getAllRooms() {
        return activeRooms.values();
    }
    
    /**
     * 참가자 입장 처리 (DB 조회 없이 간단하게 처리)
     */
    public Participant joinParticipant(Long roomId, Long userId, String nickname) {
        GameRoom room = findRoomById(roomId);
        if (room == null) return null;
        
        // 이미 있는지 확인
        if (room.getParticipants().containsKey(userId)) {
            return room.getParticipant(userId);
        }
        
        // 정원 초과 확인
        if (room.getCurrentParticipantCount() >= room.getMaxParticipants()) {
            throw new IllegalStateException("방이 꽉 찼습니다.");
        }

        // 참가자 객체 생성 (DB 조회 대신 전달받은 정보 사용)
        Participant p = new Participant(userId, nickname);
        room.addParticipant(p);
        
        
        //방정보를 갱신한다.
        if (lobbyHandler != null) {
            lobbyHandler.broadcastRoomList(); 
        }
        
        return p;
    }
    
    public void removeParticipant(Long roomId, Long userId) {
        GameRoom room = findRoomById(roomId);
        if (room != null) {
            room.removeParticipant(userId);
            
            if (room.getCurrentParticipantCount() == 0) {
                activeRooms.remove(roomId); // 사람 없으면 방 삭제
                System.out.println("🗑️ 빈 방 삭제됨: " + roomId);
                
            }
            if (lobbyHandler != null) {
                lobbyHandler.broadcastRoomList();
            }
        }
    }

}