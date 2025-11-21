package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SignUpPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // UI 컴포넌트 변수 및 MainFrame 참조
    private MainFrame parentFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton createAccountButton;
    private JButton toLogInButton;
    private JLabel signUpLabel;

    // --- 1. 생성자 오버로딩 ---
    public SignUpPanel() {
        initialize(); // 기본 생성자는 디자인 로직만 실행
    }

    public SignUpPanel(MainFrame parent) {
        this(); // 기본 생성자 호출
        this.parentFrame = parent;

        // 폼 제출 및 전환 액션 연결 (MainFrame 참조가 있을 때만)
        if (parentFrame != null) {
            createAccountButton.addActionListener(e -> handleSignUp());
            toLogInButton.addActionListener(e -> parentFrame.showPanel(MainFrame.LOGIN_SCREEN));
        }
    }

    // --- 2. 폼 제출 로직 ---
    private void handleSignUp() {
        if (parentFrame != null) {
            // TODO: 약관 동의 확인 및 서버 전송 로직 구현
            JOptionPane.showMessageDialog(this, "회원가입 요청 처리 중...", "요청", JOptionPane.INFORMATION_MESSAGE);
            // 성공 시, 다음 화면으로 전환:
            // parentFrame.showPanel(MainFrame.LOBBY_SCREEN);
        }
        // JPasswordField는 반드시 지워주세요.
        Arrays.fill(passwordField.getPassword(), '0');
    }

    // --- 3. UI 컴포넌트 초기화 및 반응형 레이아웃 설정 ---
    private void initialize() {
        // [A] 루트 패널 설정: 중앙 정렬을 위한 GridBagLayout
        setBackground(new Color(240, 240, 240));
        setLayout(new GridBagLayout());

        // [B] 하얀색 카드 영역 (메인 폼 전체 컨테이너)
        JPanel whiteCardPanel = new JPanel(new BorderLayout(0, 20));
        whiteCardPanel.setBackground(Color.WHITE);
        whiteCardPanel.setPreferredSize(new Dimension(400, 480));
        whiteCardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. 헤더 영역 ('로그인' 버튼 포함)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerPanel.setOpaque(false);
        toLogInButton = new JButton("로그인");
        toLogInButton.setPreferredSize(new Dimension(80, 25));
        headerPanel.add(toLogInButton);
        whiteCardPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. 폼 내용 영역 (GridBagLayout으로 수직 정렬)
        JPanel formContentPanel = new JPanel(new GridBagLayout());
        formContentPanel.setOpaque(false);
        whiteCardPanel.add(formContentPanel, BorderLayout.CENTER);

        // GBL 설정 시작
        int row = 0;

        // --- GBC를 매번 새로 생성하여 디자이너 에러 방지 ---

        // 2-1. Title: "Sign Up for Quiz"
        signUpLabel = new JLabel("Sign Up for Quiz");
        signUpLabel.setFont(new Font("Serif", Font.BOLD, 20));
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.insets = new Insets(5, 0, 5, 0); gbc_title.gridx = 0; gbc_title.gridy = row++;
        gbc_title.gridwidth = 2; gbc_title.weightx = 1.0; gbc_title.anchor = GridBagConstraints.CENTER;
        formContentPanel.add(signUpLabel, gbc_title);

        // 2-2. Email Title
        JLabel emailTitle = new JLabel("EMAIL ADDRESS");
        emailTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        GridBagConstraints gbc_emailTitle = new GridBagConstraints();
        gbc_emailTitle.insets = new Insets(5, 0, 5, 0); gbc_emailTitle.gridx = 0; gbc_emailTitle.gridy = row++;
        gbc_emailTitle.gridwidth = 2; gbc_emailTitle.weightx = 1.0; gbc_emailTitle.anchor = GridBagConstraints.WEST;
        formContentPanel.add(emailTitle, gbc_emailTitle);

        // 2-3. Email Field
        emailField = new JTextField("java2@ewha.ac.kr");
        emailField.setPreferredSize(new Dimension(100, 35));
        GridBagConstraints gbc_emailField = new GridBagConstraints();
        gbc_emailField.insets = new Insets(5, 0, 5, 0); gbc_emailField.gridx = 0; gbc_emailField.gridy = row++;
        gbc_emailField.gridwidth = 2; gbc_emailField.weightx = 1.0; gbc_emailField.fill = GridBagConstraints.HORIZONTAL;
        formContentPanel.add(emailField, gbc_emailField);

        // 2-4. Password Title
        JLabel passwordTitle = new JLabel("PASSWORD");
        passwordTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        GridBagConstraints gbc_passwordTitle = new GridBagConstraints();
        gbc_passwordTitle.insets = new Insets(5, 0, 5, 0); gbc_passwordTitle.gridx = 0; gbc_passwordTitle.gridy = row++;
        gbc_passwordTitle.gridwidth = 2; gbc_passwordTitle.weightx = 1.0; gbc_passwordTitle.anchor = GridBagConstraints.WEST;
        formContentPanel.add(passwordTitle, gbc_passwordTitle);

        // 2-5. Password Field (JPasswordField 사용)
        passwordField = new JPasswordField("********");
        passwordField.setPreferredSize(new Dimension(100, 35));
        GridBagConstraints gbc_passwordField = new GridBagConstraints();
        gbc_passwordField.insets = new Insets(5, 0, 5, 0); gbc_passwordField.gridx = 0; gbc_passwordField.gridy = row++;
        gbc_passwordField.gridwidth = 2; gbc_passwordField.weightx = 1.0; gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
        formContentPanel.add(passwordField, gbc_passwordField);

        // 2-6. Checkbox
        JCheckBox agreementCheckbox = new JCheckBox("I agree to the Terms of Service and Privacy Policy.");
        agreementCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 10));
        agreementCheckbox.setBackground(Color.WHITE);
        GridBagConstraints gbc_checkbox = new GridBagConstraints();
        gbc_checkbox.insets = new Insets(5, 0, 5, 0); gbc_checkbox.gridx = 0; gbc_checkbox.gridy = row++;
        gbc_checkbox.gridwidth = 2; gbc_checkbox.weightx = 1.0; gbc_checkbox.anchor = GridBagConstraints.WEST;
        formContentPanel.add(agreementCheckbox, gbc_checkbox);

        // 2-7. Button (CREATE AN ACCOUNT)
        createAccountButton = new JButton("CREATE AN ACCOUNT");
        createAccountButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setBackground(Color.BLACK);
        createAccountButton.setOpaque(true);
        createAccountButton.setBorderPainted(false);
        createAccountButton.setPreferredSize(new Dimension(100, 45));

        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.insets = new Insets(20, 0, 0, 0); gbc_button.gridx = 0; gbc_button.gridy = row++;
        gbc_button.gridwidth = 2; gbc_button.weightx = 1.0;
        gbc_button.fill = GridBagConstraints.HORIZONTAL;
        gbc_button.weighty = 1.0; // 폼 아래의 모든 남는 수직 공간을 가져가 중앙을 유지
        formContentPanel.add(createAccountButton, gbc_button);

        // [D] 최종 조립: WhiteCardPanel을 루트 패널의 중앙에 배치
        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.anchor = GridBagConstraints.CENTER;
        rootGbc.weightx = 1.0;
        rootGbc.weighty = 1.0;
        add(whiteCardPanel, rootGbc);
    }
}