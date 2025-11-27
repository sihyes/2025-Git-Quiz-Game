package kr.ac.ewha.java2.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.ewha.java2.domain.entity.AppUser;
import kr.ac.ewha.java2.domain.entity.Question;
import kr.ac.ewha.java2.domain.pojo.GameRoom;
import kr.ac.ewha.java2.dto.NewQuestionResponseDto;
import kr.ac.ewha.java2.service.GameRoomService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

	private final GameRoomService gameRoomService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// 방 번호(Long) 별로 접속한 세션 리스트를 관리
	// Key: RoomId, Value: List<Session>
	private static final Map<Long, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

	// Session별 RoomId, UserId 매핑 (퇴장 처리용)
	private static final Map<WebSocketSession, Long> sessionRoomMap = new ConcurrentHashMap<>();
	private static final Map<WebSocketSession, Long> sessionUserMap = new ConcurrentHashMap<>();

	public GameWebSocketHandler(GameRoomService gameRoomService) {
		this.gameRoomService = gameRoomService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 1. URL에서 RoomId 추출 (예: ws://localhost/ws/game/1 -> roomId = 1)
		Long roomId = extractRoomId(session);

		// 세션에서 로그인한 유저정보를 가져옵니다.
		Map<String, Object> attrs = session.getAttributes();
		AppUser user = (AppUser) attrs.get("user");

		if (user == null) {
			System.out.println("로그인 정보 없음. 연결 종료.");
			session.close();
			return;
		}
			
		System.out.println("🟢 [GameWS] 연결 성공: 방 " + roomId + ", 유저 " + user.getNickname());
		// ★ 서비스에 참가자 등록 요청
		gameRoomService.joinParticipant(roomId, user.getId(), user.getNickname());

		// 세션관리 등록
		List<WebSocketSession> sessions = roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>());
		sessions.add(session);
		sessionRoomMap.put(session, roomId);
		sessionUserMap.put(session, user.getId());

		// 나갈 때를 위해 매핑 저장
		broadcastRoomInfo(roomId);
	}

	// ★ 방 정보(참가자 목록 포함) 브로드캐스트 헬퍼 메서드 추가
	private void broadcastRoomInfo(Long roomId) {
		GameRoom room = gameRoomService.findRoomById(roomId);
		if (room == null) return;

		// 보낼 메시지 구성 (JSON)
		// 예: { "type": "ROOM_UPDATE", "participants": [ ... ] }
		Map<String, Object> msg = new HashMap<>();
		msg.put("type", "ROOM_UPDATE");
		msg.put("participants", room.getParticipants().values());

		try {
			String jsonMsg = objectMapper.writeValueAsString(msg);
			TextMessage message = new TextMessage(jsonMsg);
			
			System.out.println("📢 [GameWS] 방(" + roomId + ") 업데이트 전송: " + room.getCurrentParticipantCount() + "명");

			// 해당 방의 모든 세션에게 전송
			List<WebSocketSession> sessions = roomSessions.get(roomId);
			if (sessions != null) {
				for (WebSocketSession s : sessions) {
					if (s.isOpen())
						s.sendMessage(message);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcastQuestion(Long roomId, Question question){
		List<WebSocketSession> sessions = roomSessions.get(roomId);
		if (sessions != null) {
			try{
				NewQuestionResponseDto newQuestion = new NewQuestionResponseDto(roomId, question.getQuestionText());
				String jsonMsg = objectMapper.writeValueAsString(newQuestion);

				for (WebSocketSession s : sessions) {
					if (s.isOpen()) {
						try {
							s.sendMessage(new TextMessage(jsonMsg));
						} catch (Exception e) {
						}
					}
				}
			} catch (Exception e) {
                throw new RuntimeException(e);
            }
		}
	}

	private void broadcastToRoom(Long roomId, String msg) {
		List<WebSocketSession> sessions = roomSessions.get(roomId);
		if (sessions != null) {
			for (WebSocketSession s : sessions) {
				if (s.isOpen()) {
					try {
						s.sendMessage(new TextMessage(msg));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Long roomId = extractRoomId(session);
		String payload = message.getPayload();

		// 메시지 내용을 살짝 열어봄 (로그용)
		System.out.println("📩 게임방(" + roomId + ") 메시지: " + payload);

		// ✅ 핵심: 같은 방에 있는 사람들에게만 메시지 전송 (브로드캐스트)
		List<WebSocketSession> sessions = roomSessions.get(roomId);
		if (sessions != null) {
			for (WebSocketSession s : sessions) {
				if (s.isOpen()) {
					try {
						s.sendMessage(message); // 받은 메시지 그대로 전달 (Echo)
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Long roomId = sessionRoomMap.get(session);
		Long userId = sessionUserMap.get(session);

		if (roomId != null && userId != null) {
			System.out.println("🔴 [GameWS] 연결 종료: 방 " + roomId + ", 유저ID " + userId);
			// 1. 서비스에서 참가자 제거
			gameRoomService.removeParticipant(roomId, userId);
			// 리스트에서 퇴장한 세션 제거
			List<WebSocketSession> sessions = roomSessions.get(roomId);
			if (sessions != null) {
				sessions.remove(session);
				// 방에 아무도 없으면 방 삭제
				if (sessions.isEmpty()) {
					roomSessions.remove(roomId);
				}
			}
			
			// 매핑 제거
            sessionRoomMap.remove(session);
            sessionUserMap.remove(session);

			broadcastRoomInfo(roomId);
		}
	}

	// URL 경로에서 방 번호를 추출하는 헬퍼 메서드
	private Long extractRoomId(WebSocketSession session) {
		try {
			URI uri = session.getUri();
			String path = uri.getPath(); // "/ws/game/123"
			String[] segments = path.split("/");
			return Long.parseLong(segments[segments.length - 1]); // 맨 마지막 숫자가 RoomId
		} catch (Exception e) {
			throw new IllegalArgumentException("잘못된 웹소켓 경로입니다: " + session.getUri());
		}
	}
}