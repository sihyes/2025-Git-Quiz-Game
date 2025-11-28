package kr.ac.ewha.java2.service;

import kr.ac.ewha.java2.domain.entity.Question;
import kr.ac.ewha.java2.domain.pojo.GameRoom;
import kr.ac.ewha.java2.domain.pojo.GameState;
import kr.ac.ewha.java2.domain.pojo.Participant;
import kr.ac.ewha.java2.domain.repository.AppUserRepository;
import kr.ac.ewha.java2.domain.repository.QuestionRepository;
import kr.ac.ewha.java2.global.handler.GameWebSocketHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class GamePlayService {
    private final QuestionRepository questionRepository;
    private final AppUserRepository appUserRepository;
    private final GameRoomService gameRoomService;
    private final GameWebSocketHandler gameWebSocketHandler;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);//타이머 만들기에 사용
    private final Map<Long, ScheduledFuture<?>> roomTimers = new ConcurrentHashMap<>();

    @Lazy
    public GamePlayService(QuestionRepository questionRepository, AppUserRepository appUserRepository, GameRoomService gameRoomService, GameWebSocketHandler gameWebSocketHandler) {
        this.questionRepository = questionRepository;
        this.appUserRepository = appUserRepository;
        this.gameRoomService = gameRoomService;
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    //문제 내기
    public void sendQuestion(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        Question currentQuestion = room.getCurrentQuestion();

        int timeLimit = room.getTimeLimitPerQuestion();
        if(currentQuestion==null){
            endGame(roomId);
            return;
        }
        gameWebSocketHandler.broadcastQuestion(roomId, currentQuestion, timeLimit);
        starQuestionTimer(roomId);
    }

    //게임 종료
    public void endGame(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        if(room != null&& room.getState()==GameState.PLAYING){
            room.setState(GameState.FINISHED);
            gameWebSocketHandler.broadcastGameEnd(roomId);
        }
    }

    //게임 시작 후 대기 타이머...
    public void startInitialDelayTimer(Long roomId) {
        GameRoom room = gameRoomService.findRoomById(roomId);
        if(room != null&& room.getState()== GameState.WAITING){
            room.setState(GameState.PLAYING);
            int questionCount = room.getQuestionCount();
            List<Question> questionList = questionRepository.findRandomQuestions(questionCount);
            room.setQuestions(questionList);
            room.setCurrentQuestionIndex(0);
        }

        // 10초로 고정된 대기 시간
        final int INITIAL_DELAY_SECONDS = 10;

        // 시간이 만료 시 실행할 작업(Runnable) 정의
        Runnable task = () -> {
            // 10초 대기가 끝나면 첫 번째 문제를 제출하는 로직 호출
            sendQuestion(roomId);
        };

        // scheduler를 사용하여 10초 후에 작업 예약
        ScheduledFuture<?> future = scheduler.schedule(
                task,
                INITIAL_DELAY_SECONDS, // 10초
                TimeUnit.SECONDS
        );
    }

    //타이머...
    public void starQuestionTimer(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        //문제 출제 전 대기
        int countDown = room.getTimeLimitPerQuestion();

        ScheduledFuture<?> future = roomTimers.remove(roomId);
        if (future != null) {
            future.cancel(true); // 예약된 작업을 취소 (실행 중이어도 중단)
        }
        //시간 만료 시 실행할 작업(Runnable) 정의
        Runnable task = () -> {
            // 시간이 만료되면 실행할 로직
            proceedToNextQuestion(roomId);
        };

        // scheduler를 사용하여 작업 예약 및 Future 객체 획득
        future = scheduler.schedule(
                task,
                countDown,
                TimeUnit.SECONDS // timeLimit을 '초' 단위로 예약
        );

        // roomTimers 맵에 저장
        roomTimers.put(roomId, future);
    }

    //답 제출
    public boolean submitAnswer(Long roomId,Participant participant,  String submittedAnswer){
        try {
            GameRoom room = gameRoomService.findRoomById(roomId);
            Question question = room.getCurrentQuestion();

            if(question==null||submittedAnswer==null){
                System.out.println("null 발생.. 문제 = +"+question);
                return false;
            }
            System.out.println("[디버그] 정답 체크 - Sumitted: "+submittedAnswer+", Answer: "+question.getAnswer());
            boolean isCorrect = false;
            try {
                isCorrect = checkSubmittedAnswer(participant, question, submittedAnswer);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("checkSubmittedAnswer 실행 중 예외 발생");
                return false;
            }
            if(isCorrect){
                int points = question.getScore();
                calculateScore(participant, question, submittedAnswer);
                appUserRepository.updateParticipantScore(participant.getUserId(), participant.getScore());
                System.out.println("✅ " + participant.getNickname() + " 정답! +" + points);
            }
            else{
                System.out.println("❌ " + participant.getNickname() + " 오답 제출.");
            }
            return isCorrect;
        }catch (Exception e){
            System.out.println("Submit Answer Failed");
            e.printStackTrace();
            return false;
        }

    }

    //점수 계산
    public void calculateScore(Participant participant, Question question, String submittedAnswer){
        if(checkSubmittedAnswer(participant, question, submittedAnswer))
            participant.setScore(participant.getScore()+question.getScore());
    }

    //답 확인
    public boolean checkSubmittedAnswer(Participant participant, Question question, String submittedAnswer){
        if(submittedAnswer==null)
            return false;
        if(submittedAnswer.equals(question.getAnswer()) ){
            return true;
        }
        else return false;
    }

    //다음 문제
    public void proceedToNextQuestion(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        room.setCurrentQuestionIndex(room.getCurrentQuestionIndex()+1);
        sendQuestion(roomId);
    }

}
