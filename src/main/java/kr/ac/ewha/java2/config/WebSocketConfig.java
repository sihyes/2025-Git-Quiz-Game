package kr.ac.ewha.java2.config;
//소켓 통신 환경 설정, 관리

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//이 클래스가 Spring 구성 파일이며, 웹소켓 사용할 것임을 선언
@Configuration //이 클래스가 Spring의 설정 파일(Configuration Bean)
@EnableWebSocket//WebSocket 기능 활성화

//인터페이스 구현 -> WebSocket 핸들러 등록 및 관련 설정 구성에 필요한
//메서드(registerWebSocketHandlers)를 구현해야함을 명시
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;
    //Spring은 애플리케이션이 시작될 때 Bean으로 등록된
    //WebSocketHandler 구현체를 찾아서 생성자의 인수로 전달
    public WebSocketConfig(WebSocketHandler webSocketHandler){
        this.webSocketHandler=webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/websockt-endpoint").setAllowedOrigins("*");
    }
}
