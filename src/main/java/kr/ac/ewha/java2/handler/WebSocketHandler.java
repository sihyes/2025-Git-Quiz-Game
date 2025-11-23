package kr.ac.ewha.java2.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    //메시지 수신 처리
    @Override
    protected  void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception{
        String payload = textMessage.getPayload();
        System.out.println("수신된 메시지: "+payload);
        //화면에 메시지 표시 역할
        session.sendMessage(new TextMessage("서버 응답: "+payload));
    }
    //연결 수립 처리 -> 새로운 클라이언트가 접속했음을 알림
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        System.out.println(session.getId()+" 클라이언트 접속");
    }
    //연결 종료 처리 -> 접속 해제 알림
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(session.getId()+" 클라이언트 접속 해제");
    }

}

