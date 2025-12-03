package kr.ac.ewha.java2.service;

import kr.ac.ewha.java2.domain.entity.AppUser;
import kr.ac.ewha.java2.domain.entity.Question;
import kr.ac.ewha.java2.domain.pojo.GameRoom;
import kr.ac.ewha.java2.domain.pojo.GameState;
import kr.ac.ewha.java2.domain.pojo.Participant;
import kr.ac.ewha.java2.domain.repository.AppUserRepository;
import kr.ac.ewha.java2.domain.repository.QuestionRepository;
import kr.ac.ewha.java2.global.handler.GameWebSocketHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

@Service
public class GamePlayService {
    private final QuestionRepository questionRepository;
    private final AppUserRepository appUserRepository;
    private final GameRoomService gameRoomService;
    private final GameWebSocketHandler gameWebSocketHandler;
    private final AppUserService appUserService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);//타이머 만들기에 사용
    private final Map<Long, ScheduledFuture<?>> roomTimers = new ConcurrentHashMap<>();
    //다음 문제 3초 대기
    final int INTERMISSION_DELAY_SECONDS = 5;
    final int INITIAL_DELAY_SECONDS = 10;

    @Lazy
    public GamePlayService(QuestionRepository questionRepository, AppUserRepository appUserRepository,
                           GameRoomService gameRoomService, GameWebSocketHandler gameWebSocketHandler, AppUserService appUserService) {
        this.questionRepository = questionRepository;
        this.appUserRepository = appUserRepository;
        this.gameRoomService = gameRoomService;
        this.gameWebSocketHandler = gameWebSocketHandler;
        this.appUserService = appUserService;
    }

    //문제 내기
    public void sendQuestion(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        Question currentQuestion = room.getCurrentQuestion();

        int timeLimit = room.getTimeLimitPerQuestion();
        //문제 없을 경우
        if(currentQuestion==null){
            System.out.println("[END] 문제 소진 -> 게임 종료");
            endGame(roomId);
            return;
        }
        room.getAnsweredUserIds().clear();//정답자 id 초기화
        //문제 브로드캐스트
        gameWebSocketHandler.broadcastQuestion(roomId, currentQuestion, timeLimit);
        //문제 타이머 시작
        starQuestionTimer(roomId);
    }

    //게임 종료
    public void endGame(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        if(room != null&& room.getState()==GameState.PLAYING){
            room.setState(GameState.FINISHED);
            //Map<Long, Participant> participants 순서 있는 리스트로 가져와서 사용
            List<Participant> finalRank = new ArrayList<>(room.getParticipants().values());
            //점수 내림차순 정렬
            finalRank.sort(Comparator.comparing(Participant::getScore).reversed());
            System.out.println("최종 참가자 수: "+finalRank.size());
            //디버그용 출력문
            for(Participant p: finalRank){
                System.out.println(p.getNickname()+"의 점수: "+p.getScore());
            }
            gameWebSocketHandler.broadcastGameEnd(roomId, finalRank);
            try{
                Thread.sleep(4000);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
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

        // 시간 만료 시 실행할 작업
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
        // roomTimers 맵에 저장
        roomTimers.put(roomId, future);
    }

    //타이머...
    public void starQuestionTimer(Long roomId){
        GameRoom room = gameRoomService.findRoomById(roomId);
        //문제 출제 전 대기
        int countDown = room.getTimeLimitPerQuestion();
        //이전 타이머 취소
        ScheduledFuture<?> future = roomTimers.remove(roomId);
        if (future != null) {
            future.cancel(true);
        }
        // 시간 만료 시 실행할 작업
        Runnable task = () -> {
            try {
                proceedToNextQuestion(roomId);
            } catch (Exception e) {
                System.out.println("[TIME ERROR]");
                e.printStackTrace();
                endGame(roomId);
            }
        };
        //ScheduledExecutorService
        // scheduler를 사용하여 작업 예약 및 Future 객체 획득
        future = scheduler.schedule(
                task,// 시간 만료 시 실행할 작업
                countDown,// 기다릴 시간
                TimeUnit.SECONDS // 시간 단위
        );

        // roomTimers 맵에 저장
        roomTimers.put(roomId, future);
    }
    //인터미션 타이머...
    public void starIntermissionTimer(Long roomId) {
        gameWebSocketHandler.broadcastIntermission(roomId, INTERMISSION_DELAY_SECONDS);
        Runnable task = () -> {
            try {
                proceedToNextQuestion(roomId);
            } catch (Exception e) {
                System.out.println("[TIME ERROR]");
                e.printStackTrace();
                endGame(roomId);
            }
        };
        ScheduledFuture<?> future = roomTimers.remove(roomId);
        if (future != null) {
            future.cancel(true);
        }
        future = scheduler.schedule(
                task,
                INTERMISSION_DELAY_SECONDS,
                TimeUnit.SECONDS
        );

        // roomTimers 맵에 저장
        roomTimers.put(roomId, future);
    }
    //답 제출
    @Transactional
    public boolean submitAnswer(Long roomId,Participant participant,  String submittedAnswer){
        try {
            GameRoom room = gameRoomService.findRoomById(roomId);
            Question question = room.getCurrentQuestion();

            if(question==null||submittedAnswer==null){
                System.out.println("null 발생.. 문제 = +"+question);
                return false;
            }
            //정답자가 정답 다시 맞출 경우
            if(room.getAnsweredUserIds().contains(participant.getUserId())){
                System.out.println(participant.getNickname()+": 정답 이미 맞춤");
                return false;
            }

            System.out.println("[디버그] 정답 체크 - Submitted: "+submittedAnswer+", Answer: "+question.getAnswer());
            boolean isCorrect = false;
            try {
                isCorrect = checkSubmittedAnswer(participant, question, submittedAnswer);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("checkSubmittedAnswer 실행 중 예외 발생");
                return false;
            }
            //정답일 경우
            if(isCorrect){
                int points = question.getScore();
                //점수 계산
                calculateScore(participant, question, submittedAnswer);
                //DB 업데이트
                appUserService.updateScore(participant.getUserId(), points);
                //정답자 Set에 추가
                room.getAnsweredUserIds().add(participant.getUserId());
                //모두 정답 맞혔는지 확인
                if(checkAllParticipantsAnswered(room)){
                    System.out.println("모든 참가자가 정답 제출");
                    ScheduledFuture<?> currentTimer = roomTimers.get(roomId);
                    //타이머가 작동중일 경우
                    if(currentTimer!=null){
                        currentTimer.cancel(false);
                    }
                    starIntermissionTimer(roomId);
                    return true;
                }
                System.out.println("✅ " + participant.getNickname() + " 정답! +" + points);
            }
            else{
                System.out.println("❌ " + participant.getNickname() + " 오답 제출");
            }
            return isCorrect;
        }catch (Exception e){
            System.out.println("Submit Answer Failed");
            e.printStackTrace();
            return false;
        }

    }

    //모든 참가자 정답 맞혔는지 확인
    public boolean checkAllParticipantsAnswered(GameRoom room){
        if(room.getCurrentParticipantCount()==room.getAnsweredUserIds().size()){
            return true;
        }
        else return false;
    }

    //점수 계산
    public void calculateScore(Participant participant, Question question, String submittedAnswer){
        if(checkSubmittedAnswer(participant, question, submittedAnswer)) {
            participant.setScore(participant.getScore() + question.getScore());
        }
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
