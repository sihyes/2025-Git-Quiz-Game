package kr.ac.ewha.java2.global.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.ac.ewha.java2.domain.pojo.ChatMessage;
import kr.ac.ewha.java2.domain.pojo.GameRoom;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	
	// 방별 세션 매핑: roomId -> WebSocketSession 리스트
    private final Map<Long, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);
		System.out.println("✅ 클라이언트 연결됨: " + session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		ChatMessage chatMessage = new ObjectMapper().readValue(payload, ChatMessage.class);

		for (WebSocketSession s : sessions) {
			if (s.isOpen()) {  //연결이 끊기지 않도록 추가.
				try {
					s.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(chatMessage)));
				} catch (IOException e) {
					System.out.println("예외발생. ");
					e.printStackTrace();
				}
			}else {
				 System.out.println("❌ 세션이 닫혀 있음: " + s.getId());

			}
		}
	}


	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
	    sessions.remove(session);
	    System.out.println("🔌 연결 해제: " + session.getId());
	}

	public List<WebSocketSession> getSessions() {
		// TODO Auto-generated method stub
		return sessions;
	}
	// 특정 방에 세션 추가
    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    // 특정 방에 세션 브로드캐스트
    public void sendMessageToRoom(Long roomId, String message) {
        List<WebSocketSession> roomList = roomSessions.get(roomId);
        if (roomList == null) return;
        for (WebSocketSession s : roomList) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

