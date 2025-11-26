package kr.ac.ewha.java2.controller;

import jakarta.servlet.http.HttpSession;
import kr.ac.ewha.java2.domain.entity.AppUser;
import kr.ac.ewha.java2.domain.pojo.GameRoom;
import kr.ac.ewha.java2.dto.CreateRoomRequestDto;
import kr.ac.ewha.java2.service.GameRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/rooms")
public class GameController {

    private final GameRoomService gameRoomService;

    public GameController(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    /**
     * 방 생성 API
     * 요청: POST /api/rooms { "title": "즐거운 퀴즈" }
     */
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequestDto request, HttpSession session) {
    	Object sessionUser = session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        AppUser user = (AppUser) sessionUser;
        GameRoom newRoom = gameRoomService.createRoom(request, user.getId(), user.getNickname());
 
        return ResponseEntity.ok(newRoom);
    }

    /**
     * 전체 방 목록 조회 API
     * 요청: GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<Collection<GameRoom>> getAllRooms() {
        return ResponseEntity.ok(gameRoomService.getAllRooms());
    }
    
    /**
     * 특정 방 정보 조회 API
     * 요청: GET /api/rooms/1
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable Long roomId) {
        GameRoom room = gameRoomService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }
}