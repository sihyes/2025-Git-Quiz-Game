package kr.ac.ewha.java2.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import kr.ac.ewha.java2.global.handler.GameWebSocketHandler;
import kr.ac.ewha.java2.global.handler.LobbyWebSocketHandler;
import kr.ac.ewha.java2.global.handler.WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;
    private final LobbyWebSocketHandler lobbyHandler; // 로비용
    private final GameWebSocketHandler gameHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler,LobbyWebSocketHandler lobbyHandler, GameWebSocketHandler gameHandler) {
        this.webSocketHandler = webSocketHandler;
        this.lobbyHandler = lobbyHandler;
		this.gameHandler = gameHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	// 1. 기존 채팅/게임방 연결
        registry.addHandler(webSocketHandler, "/ws/chat").setAllowedOrigins("*");
        
        // 2. ✅ 로비 연결 (이 부분이 없어서 에러가 났던 것입니다!)
        registry.addHandler(lobbyHandler, "/ws/lobby")
        .addInterceptors(new HttpSessionHandshakeInterceptor())
        .setAllowedOrigins("*");
        
        // 3. (나중에 추가할) 게임별 연결
        registry.addHandler(gameHandler, "/ws/game/*")
        .addInterceptors(new HttpSessionHandshakeInterceptor())
        .setAllowedOrigins("*");
    }
}

