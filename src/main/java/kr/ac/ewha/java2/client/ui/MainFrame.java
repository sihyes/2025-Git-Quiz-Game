package kr.ac.ewha.java2.client.ui;


import java.awt.CardLayout;
import java.awt.EventQueue; // main 메소드에 필요
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel cardPanel; // 모든 화면(패널)을 담는 컨테이너
    private CardLayout cardLayout;

    // 상수로 정의된 화면 이름표
    public static final String TITLE_SCREEN = "TITLE";
    public static final String LOGIN_SCREEN = "LOGIN";
    // MainFrame.java 클래스 내부 (기존 상수 TITLE_SCREEN, LOGIN_SCREEN 옆에 추가)
    public static final String ENTER_ROOM_SCREEN = "ENTER_ROOM";
    public static final String CREATE_ROOM_SCREEN = "CREATE_ROOM";
    public static final String SIGNUP_SCREEN = "SIGNUP";
    public static final String LOBBY_SCREEN = "LOBBY"; // 다음 화면
    public static final String ROOM_SETTING_SCREEN = "ROOM_SETTING";
    public static final String ROOM_SCREEN = "Room";
    public static final String QUIZ_SCREEN = "Quiz";
    public static final String RESULT_SCREEN = "Result";


    /**
     * Launch the application. (실행 시작점)
     */
    public static void main(String[] args) {
        // Swing GUI는 EDT(Event Dispatch Thread)에서 실행해야 합니다.
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // MainFrame 객체를 생성하고 보이게 합니다.
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Git Quiz Game Client");
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout(0, 0);
        cardPanel = new JPanel();
        cardPanel.setLayout(cardLayout);

        setContentPane(cardPanel);

        // ==========================================================
        // 🃏 화면(패널)들을 CardPanel에 추가
        // ==========================================================

        // TitlePanel과 LoginPanel은 매개변수 없는 생성자도 가지고 있어야
        // WindowBuilder 디자인 뷰에서 에러가 나지 않습니다. (이전 대화에서 해결)
        TitlePanel titlePanel = new TitlePanel(this);
        LoginPanel loginPanel = new LoginPanel(this);
        SignUpPanel signUpPanel = new SignUpPanel(this);
        LobbyPanel lobbyPanel = new LobbyPanel(this);

        cardPanel.add(titlePanel, TITLE_SCREEN);
        cardPanel.add(loginPanel, LOGIN_SCREEN);
        cardPanel.add(signUpPanel, SIGNUP_SCREEN);
        cardPanel.add(lobbyPanel, MainFrame.LOBBY_SCREEN);
        cardPanel.add(new RoomPanel(this), ROOM_SCREEN);
        cardPanel.add(new RoomSettingPanel(this), ROOM_SETTING_SCREEN);
        cardPanel.add(new QuizPanel(this), QUIZ_SCREEN);
        cardPanel.add(new ResultPanel(this), RESULT_SCREEN);

        // 초기 화면 설정 (TITLE 화면부터 시작)
        cardLayout.show(cardPanel, TITLE_SCREEN);
    }

    /**
     * 외부 Panel에서 화면 전환을 요청할 때 사용하는 공개 메서드
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
}