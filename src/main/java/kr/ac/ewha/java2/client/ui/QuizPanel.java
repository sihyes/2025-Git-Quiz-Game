package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuizPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private MainFrame parentFrame;

    // --- 상태 관리 변수 ---
    private final int totalQuestions = 15; // 총 문제 수
    private int currentQuestionIndex = 1;  // 현재 문제 번호

    // --- UI 컴포넌트 (업데이트가 필요한 것들) ---
    private JLabel questionLabel;     // 문제 텍스트
    private JTextField answerField;   // 정답 입력창
    private JProgressBar progressBar; // 하단 진행바
    private JLabel progressText;      // 1/15 텍스트

    // --- 색상 상수 ---
    private final Color COLOR_BG = new Color(240, 239, 237);
    private final Color COLOR_INPUT_BG = new Color(245, 245, 250);
    private final Color COLOR_ACTIVE_PLAYER = new Color(180, 180, 180);
    private final Color COLOR_INACTIVE_PLAYER = Color.WHITE;
    private final Color COLOR_PROGRESS = new Color(110, 200, 110);
    private final Color COLOR_BTN_SUBMIT = new Color(170, 170, 170);

    public QuizPanel(MainFrame parent) {
        this.parentFrame = parent;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // 1. 상단 (Header)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. 중앙 (Question & Input)
        add(createCenterPanel(), BorderLayout.CENTER);

        // 3. 하단 (Player Status + Footer)
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setOpaque(false);
        southContainer.add(createPlayerStatusPanel(), BorderLayout.NORTH);
        southContainer.add(createFooterPanel(), BorderLayout.SOUTH);

        add(southContainer, BorderLayout.SOUTH);
    }

    // ----------------------------------------------------
    // 1. Header Logic
    // ----------------------------------------------------
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));

        // [Left] Timer
        JPanel timerBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        timerBadge.setBackground(Color.WHITE);
        timerBadge.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        JLabel timerIcon = new JLabel("🕒");
        JLabel timerText = new JLabel("00 : 10");
        timerText.setFont(new Font("Monospaced", Font.BOLD, 16));
        timerText.setForeground(new Color(50, 50, 100));

        timerBadge.add(timerIcon);
        timerBadge.add(timerText);

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftWrap.setOpaque(false);
        leftWrap.add(timerBadge);

        // [Center] Title
        JLabel titleLabel = new JLabel("Git Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));

        // [Right] Close
        JLabel closeBtn = new JLabel("✕", SwingConstants.CENTER);
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeBtn.setPreferredSize(new Dimension(40, 40));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeWrap.setOpaque(false);
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(parentFrame != null) parentFrame.showPanel(MainFrame.LOBBY_SCREEN);
            }
        });
        closeWrap.add(closeBtn);

        header.add(leftWrap, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.add(closeWrap, BorderLayout.EAST);

        return header;
    }

    // ----------------------------------------------------
    // 2. Center Logic (문제 & 입력창)
    // ----------------------------------------------------
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // [수정] GridBagConstraints 객체 분리 (이클립스 경고 해결)

        // 1. 문제 라벨용 gbc
        GridBagConstraints gbcQuestion = new GridBagConstraints();
        gbcQuestion.gridx = 0;
        gbcQuestion.gridy = 0;
        gbcQuestion.insets = new Insets(10, 0, 10, 0);
        gbcQuestion.anchor = GridBagConstraints.CENTER;

        // 멤버 변수에 할당 (나중에 텍스트 바꿔야 하므로)
        questionLabel = new JLabel("Q" + currentQuestionIndex + ". To add a commit message: [10]");
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        centerPanel.add(questionLabel, gbcQuestion);

        // 2. 입력창용 gbc (새로 생성)
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.gridx = 0;
        gbcInput.gridy = 1;
        gbcInput.insets = new Insets(30, 0, 0, 0);
        gbcInput.anchor = GridBagConstraints.CENTER;

        // 멤버 변수에 할당
        answerField = new JTextField("your answer");
        answerField.setPreferredSize(new Dimension(400, 60));
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setBackground(COLOR_INPUT_BG);
        answerField.setBorder(BorderFactory.createEmptyBorder());

        // Placeholder 기능
        answerField.setForeground(Color.GRAY);
        answerField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (answerField.getText().equals("your answer")) {
                    answerField.setText("");
                    answerField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (answerField.getText().isEmpty()) {
                    answerField.setText("your answer");
                    answerField.setForeground(Color.GRAY);
                }
            }
        });

        centerPanel.add(answerField, gbcInput);

        return centerPanel;
    }

    // ----------------------------------------------------
    // 3. Player Status Logic (가로 정렬 수정됨)
    // ----------------------------------------------------
    private JPanel createPlayerStatusPanel() {
        JPanel statusPanel = new JPanel();
        // [핵심] BoxLayout X_AXIS 사용하여 가로로 강제 정렬
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(new EmptyBorder(20, 0, 30, 0));

        // 중앙 정렬을 위한 Glue 추가
        statusPanel.add(Box.createHorizontalGlue());

        // 카드 추가 (Box.createHorizontalStrut으로 간격 조정)
        statusPanel.add(createPlayerCard("player1: 10", false));
        statusPanel.add(Box.createHorizontalStrut(30)); // 30px 간격

        statusPanel.add(createPlayerCard("player2: 0", true));   // active (나)
        statusPanel.add(Box.createHorizontalStrut(30));

        statusPanel.add(createPlayerCard("player3 : 10", false));
        statusPanel.add(Box.createHorizontalStrut(30));

        statusPanel.add(createPlayerCard("player4: 10", false));

        statusPanel.add(Box.createHorizontalGlue());

        return statusPanel;
    }

    private JPanel createPlayerCard(String text, boolean isActive) {
        JPanel card = new JPanel(new GridBagLayout());
        // 크기 고정 (일관성 유지)
        card.setPreferredSize(new Dimension(180, 50));
        card.setMaximumSize(new Dimension(180, 50));
        card.setMinimumSize(new Dimension(180, 50));

        card.setBackground(isActive ? COLOR_ACTIVE_PLAYER : COLOR_INACTIVE_PLAYER);

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        card.add(label);

        return card;
    }

    // ----------------------------------------------------
    // 4. Footer Logic (진행바 및 제출 로직)
    // ----------------------------------------------------
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(new EmptyBorder(15, 100, 15, 100));

        // [Left] Progress Bar
        JPanel progressWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        progressWrap.setOpaque(false);

        progressBar = new JProgressBar(0, totalQuestions);
        progressBar.setValue(currentQuestionIndex);
        progressBar.setPreferredSize(new Dimension(200, 10));
        progressBar.setForeground(COLOR_PROGRESS);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setBorderPainted(false);

        progressText = new JLabel(currentQuestionIndex + "/" + totalQuestions);
        progressText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        progressText.setForeground(Color.GRAY);

        progressWrap.add(progressBar);
        progressWrap.add(progressText);

        // [Right] Submit Button
        JButton submitButton = new JButton("제출하기");
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.setBackground(COLOR_BTN_SUBMIT);
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);

        // [핵심] 버튼 로직 구현
        submitButton.addActionListener(e -> {
            handleSubmit();
        });

        footer.add(progressWrap, BorderLayout.WEST);
        footer.add(submitButton, BorderLayout.EAST);

        return footer;
    }

    // 버튼 클릭 시 실행되는 로직
    private void handleSubmit() {
        // 1. 입력 확인
        String answer = answerField.getText();
        if (answer.trim().isEmpty() || answer.equals("your answer")) {
            JOptionPane.showMessageDialog(this, "정답을 입력해주세요!", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 진행 상태 확인
        if (currentQuestionIndex < totalQuestions) {
            // 마지막 문제가 아니면 -> 다음 문제로 넘어감
            nextQuestion();
        } else {
            // 마지막 문제이면 -> 결과 화면으로 이동
            int choice = JOptionPane.showConfirmDialog(this,
                    "모든 문제를 풀었습니다. 제출하시겠습니까?",
                    "최종 제출",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                if (parentFrame != null) {
                    parentFrame.showPanel(MainFrame.RESULT_SCREEN);
                }
            }
        }
    }

    // 다음 문제로 UI 업데이트하는 메서드
    private void nextQuestion() {
        currentQuestionIndex++;

        // UI 값 갱신
        progressBar.setValue(currentQuestionIndex);
        progressText.setText(currentQuestionIndex + "/" + totalQuestions);

        // 문제 텍스트 변경 (나중에 배열에서 가져오게 수정 가능)
        questionLabel.setText("Q" + currentQuestionIndex + ". Next Question Text Here...");

        // 입력창 초기화
        answerField.setText("your answer");
        answerField.setForeground(Color.GRAY);
        answerField.requestFocus();
    }
}