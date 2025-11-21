package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private MainFrame parentFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton logInButton;        // '로그인'으로 변경
    private JButton toSignUpButton;     // '회원가입' 버튼으로 변경

    // --- 1. 생성자 오버로딩 (디자이너/실행 호환) ---
    public LoginPanel() {
        initialize(); // 기본 생성자는 디자인 로직만 실행
    }

    public LoginPanel(MainFrame parent) {
        this(); // 기본 생성자 호출
        this.parentFrame = parent;

        // 폼 제출 및 전환 액션 연결 (MainFrame 참조가 있을 때만)
        if (parentFrame != null) {
            logInButton.addActionListener(e -> handleLogin());

            // '회원 가입' 버튼 클릭 시 SignUp 화면으로 전환
            toSignUpButton.addActionListener(e -> parentFrame.showPanel(MainFrame.SIGNUP_SCREEN));
        }
    }

    // --- 2. 로그인 로직 ---
    private void handleLogin() {
        // TODO: 실제 서버 통신 로직은 2번 개발자가 구현

        // 닉네임, 비밀번호 확인
        // ...

        // 로그인 성공 시, 다음 화면(로비)으로 전환 요청
        if (parentFrame != null) {
            // 🚨 임시 메시지를 제거하고 전환 로직 실행
            // JOptionPane.showMessageDialog(this, "로그인 성공! 로비 화면으로 전환됩니다.", "로그인", JOptionPane.INFORMATION_MESSAGE);

            // 1. 로그인 성공 가정 후, Lobby Panel로 전환을 요청
            parentFrame.showPanel(MainFrame.LOBBY_SCREEN); // ⬅️ 이 부분 주석을 해제하고 실행합니다.
        }

        // JPasswordField는 반드시 지워주세요.
        Arrays.fill(passwordField.getPassword(), '0');
    }

    // --- 3. UI 컴포넌트 초기화 및 반응형 레이아웃 설정 ---
    private void initialize() {
        // [A] 루트 패널 설정: 중앙 정렬을 위한 GridBagLayout
        setBackground(new Color(240, 240, 240));
        setLayout(new GridBagLayout());

        // [B] 하얀색 카드 영역
        JPanel whiteCardPanel = new JPanel(new BorderLayout(0, 20));
        whiteCardPanel.setBackground(Color.WHITE);
        whiteCardPanel.setPreferredSize(new Dimension(400, 480));
        whiteCardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. 헤더 영역 ('회원가입' 버튼 포함)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerPanel.setOpaque(false);
        toSignUpButton = new JButton("회원가입"); // 텍스트 변경
        toSignUpButton.setPreferredSize(new Dimension(100, 25));
        headerPanel.add(toSignUpButton);
        whiteCardPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. 폼 내용 영역 (GridBagLayout으로 수직 정렬)
        JPanel formContentPanel = new JPanel(new GridBagLayout());
        formContentPanel.setOpaque(false);
        whiteCardPanel.add(formContentPanel, BorderLayout.CENTER);

        // GBL 설정 시작
        int row = 0;

        // GBC 재사용을 피하기 위해 매번 새로 생성
        GridBagConstraints gbc;

        // 2-1. Title: "User Log In"
        JLabel titleLabel = new JLabel("User Log In"); // 텍스트 변경
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.CENTER;
        formContentPanel.add(titleLabel, gbc);

        // 2-2. Email Title
        JLabel emailTitle = new JLabel("EMAIL ADDRESS");
        emailTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formContentPanel.add(emailTitle, gbc);

        // 2-3. Email Field
        emailField = new JTextField(); // 로그인 시에는 초기 텍스트를 비워두는 것이 일반적
        emailField.setPreferredSize(new Dimension(100, 35));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formContentPanel.add(emailField, gbc);

        // 2-4. Password Title
        JLabel passwordTitle = new JLabel("PASSWORD");
        passwordTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formContentPanel.add(passwordTitle, gbc);

        // 2-5. Password Field (JPasswordField)
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(100, 35));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formContentPanel.add(passwordField, gbc);

        // 2-6. Empty Spacer (체크박스 제거로 인한 공간 확보)
        // Login에는 약관 동의 체크박스가 없으므로, 여백을 위해 빈 공간을 추가합니다.
        gbc = new GridBagConstraints();
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 0, 5, 0);
        formContentPanel.add(Box.createVerticalStrut(10), gbc);

        // 2-7. Button (LOG IN)
        logInButton = new JButton("LOG IN"); // 텍스트 변경
        logInButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        logInButton.setForeground(Color.WHITE);
        logInButton.setBackground(Color.BLACK);
        logInButton.setOpaque(true);
        logInButton.setBorderPainted(false);
        logInButton.setPreferredSize(new Dimension(100, 45));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 0, 0); gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0; // 폼 아래의 모든 남는 수직 공간을 가져가 중앙을 유지
        formContentPanel.add(logInButton, gbc);

        // [D] 최종 조립: WhiteCardPanel을 루트 패널의 중앙에 배치
        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.anchor = GridBagConstraints.CENTER;
        rootGbc.weightx = 1.0;
        rootGbc.weighty = 1.0;
        add(whiteCardPanel, rootGbc);
    }
}
