package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RoomSettingPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private MainFrame parentFrame;

    // 설정 값 상태 관리 변수
    private int selectedQuestionCount = 10;
    private int selectedTimeLimit = 10;

    // UI 컴포넌트
    private JTextField roomTitleField;
    private JButton completeButton;

    // 버튼 색상 관리를 위한 리스트
    private List<JButton> questionButtons = new ArrayList<>();
    private List<JButton> timeButtons = new ArrayList<>();

    private final Color COLOR_SELECTED = new Color(100, 100, 255); // 선택 색상 (파랑)
    private final Color COLOR_DEFAULT = UIManager.getColor("Button.background"); // 기본 색상

    public RoomSettingPanel() {
        initialize();
    }

    public RoomSettingPanel(MainFrame parent) {
        this.parentFrame = parent;
        initialize();
        // 생성자에서 복잡한 로직 제거 -> initialize 내부에서 처리
    }

    private void initialize() {
        setBackground(new Color(240, 240, 240));
        setLayout(null);

        add(createHeaderPanel());
        add(createContentPanel());
        add(createFooterPanel());

        // 초기 버튼 상태 업데이트
        updateButtonStyles();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 450, 54);
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.setLayout(null);

        JLabel playerIcon = new JLabel("👤", SwingConstants.CENTER);
        playerIcon.setBounds(10, 10, 24, 34);
        playerIcon.setFont(new Font("Serif", Font.PLAIN, 24));
        headerPanel.add(playerIcon);

        JLabel titleLabel = new JLabel("Git Quiz", SwingConstants.CENTER);
        titleLabel.setBounds(34, 10, 376, 34);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        headerPanel.add(titleLabel);

        // 닫기 버튼 (X)
        JLabel closeButton = new JLabel("✕", SwingConstants.CENTER);
        closeButton.setBounds(410, 10, 30, 34);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 닫기 누르면 다시 RoomPanel로 복귀
                if (parentFrame != null) parentFrame.showPanel(MainFrame.ROOM_SCREEN);
            }
        });
        headerPanel.add(closeButton);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBounds(0, 54, 450, 176);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 50, 50, 50));
        contentPanel.setLayout(null);

        // 방 제목
        roomTitleField = new JTextField("방 제목");
        roomTitleField.setBounds(100, 10, 250, 35); // 높이 약간 키움
        roomTitleField.setHorizontalAlignment(JTextField.CENTER);
        contentPanel.add(roomTitleField);

        // 헤더 라벨
        JLabel qCountHeader = new JLabel("문항 개수", SwingConstants.CENTER);
        qCountHeader.setBounds(32, 50, 155, 26);
        qCountHeader.setFont(new Font("Serif", Font.BOLD, 18));
        contentPanel.add(qCountHeader);

        JLabel timeHeader = new JLabel("제한 시간", SwingConstants.CENTER);
        timeHeader.setBounds(269, 50, 155, 26);
        timeHeader.setFont(new Font("Serif", Font.BOLD, 18));
        contentPanel.add(timeHeader);

        // 문항 개수 버튼들 (10, 15, 20)
        createOptionButton(contentPanel, "10", 10, 65, 80, true);
        createOptionButton(contentPanel, "15", 15, 65, 111, true);
        createOptionButton(contentPanel, "20", 20, 65, 142, true);

        // 제한 시간 버튼들 (10, 20, 30)
        createOptionButton(contentPanel, "10", 10, 307, 80, false);
        createOptionButton(contentPanel, "20", 20, 307, 111, false);
        createOptionButton(contentPanel, "30", 30, 307, 142, false);

        return contentPanel;
    }

    // 버튼 생성 헬퍼 메서드
    private void createOptionButton(JPanel panel, String text, int value, int x, int y, boolean isQuestion) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 93, 23);

        btn.addActionListener(e -> {
            if (isQuestion) selectedQuestionCount = value;
            else selectedTimeLimit = value;
            updateButtonStyles(); // 클릭 시 색상 업데이트
        });

        panel.add(btn);

        // 리스트에 추가 (색상 관리용)
        if (isQuestion) questionButtons.add(btn);
        else timeButtons.add(btn);
    }

    // 버튼 선택 상태에 따라 색상 변경
    private void updateButtonStyles() {
        for (JButton btn : questionButtons) {
            btn.setBackground(Integer.parseInt(btn.getText()) == selectedQuestionCount ? COLOR_SELECTED : COLOR_DEFAULT);
            btn.setForeground(Integer.parseInt(btn.getText()) == selectedQuestionCount ? Color.WHITE : Color.BLACK);
        }
        for (JButton btn : timeButtons) {
            btn.setBackground(Integer.parseInt(btn.getText()) == selectedTimeLimit ? COLOR_SELECTED : COLOR_DEFAULT);
            btn.setForeground(Integer.parseInt(btn.getText()) == selectedTimeLimit ? Color.WHITE : Color.BLACK);
        }
    }

    private JPanel createFooterPanel() {
        JPanel footerBackground = new JPanel();
        footerBackground.setBounds(0, 230, 450, 70);
        footerBackground.setBackground(new Color(220, 220, 220));
        footerBackground.setLayout(null);

        completeButton = new JButton("완료");
        completeButton.setBounds(330, 15, 100, 40);
        completeButton.setBackground(new Color(150, 150, 150));
        completeButton.setForeground(Color.WHITE);
        completeButton.setOpaque(true);
        completeButton.setBorderPainted(false);

        // [중요] 완료 버튼 액션 직접 연결
        completeButton.addActionListener(e -> handleCompletion());

        footerBackground.add(completeButton);
        return footerBackground;
    }

    private void handleCompletion() {
        String roomTitle = roomTitleField.getText();

        JOptionPane.showMessageDialog(this,
                "설정 저장됨!\n[" + roomTitle + "] 문항:" + selectedQuestionCount + " / 시간:" + selectedTimeLimit,
                "설정 완료", JOptionPane.INFORMATION_MESSAGE);

        // 설정 완료 후 다시 RoomPanel로 돌아감
        if (parentFrame != null) {
            parentFrame.showPanel(MainFrame.ROOM_SCREEN);
        }
    }
}