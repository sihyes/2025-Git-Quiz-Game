package kr.ac.ewha.java2.global.handler;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.ac.ewha.java2.service.GameRoomService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LobbyWebSocketHandler extends TextWebSocketHandler {
    private final GameRoomService gameRoomService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // 로비에 접속한 사람들의 목록
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    public LobbyWebSocketHandler(@Lazy GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("🏠 로비 접속: " + session.getId());

        // 접속하자마자 현재 방 목록을 보내줌 (임시 데이터)
        broadcastRoomListToSession(session);
    }

    // 특정 세션에게만 방 목록 보내기
    private void broadcastRoomListToSession(WebSocketSession session) {
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "ROOM_LIST");
            msg.put("rooms", gameRoomService.getAllRooms()); // 실제 방 목록

            String json = objectMapper.writeValueAsString(msg);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 전체 브로드캐스트 (방 생성/삭제 시 호출됨)
    public void broadcastRoomList() {
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "ROOM_LIST");
            msg.put("rooms", gameRoomService.getAllRooms());

            String json = objectMapper.writeValueAsString(msg);
            TextMessage message = new TextMessage(json);

            for (WebSocketSession s : sessions) {
                if (s.isOpen()) s.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("🚪 로비 퇴장: " + session.getId());
    }

    // 방이 새로 생기거나 사라질 때, 로비에 있는 모든 사람에게 알리는 메서드 (추후 사용)
    public void broadcastRoomList(String roomListJson) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(roomListJson));
                } catch (IOException e) {
                    // 무시
                }
            }
        }
    }
}