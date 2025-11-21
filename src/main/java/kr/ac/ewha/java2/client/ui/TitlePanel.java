package kr.ac.ewha.java2.client.ui;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitlePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // UI 컴포넌트 변수
    private MainFrame parentFrame;
    private JLabel titleLabel;
    private JButton startButton;

    // ==========================================================
    // 1. [필수] 디자이너/기본 로딩을 위한 기본 생성자
    // ==========================================================
    public TitlePanel() {
        // 부모 프레임 없이 초기화할 때 사용합니다.
        initialize();
        // 런타임이 아닌 디자인 뷰에서는 버튼 액션을 등록하지 않습니다.
    }

    // ==========================================================
    // 2. [실행용] MainFrame에서 사용하는 생성자
    // ==========================================================
    public TitlePanel(MainFrame parent) {
        // 기본 생성자를 호출하여 컴포넌트를 먼저 생성하고 배치합니다.
        this();

        this.parentFrame = parent;

        // 3. 버튼에 화면 전환 로직을 연결 (부모 프레임이 있을 때만)
        if (parentFrame != null) {
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    parentFrame.showPanel(MainFrame.LOGIN_SCREEN);
                }
            });
        }
    }

    // ==========================================================
    // 3. UI 컴포넌트 초기화 및 반응형 레이아웃 설정
    // ==========================================================
    private void initialize() {
        // TitlePanel의 루트 레이아웃: BorderLayout으로 중앙/하단 분리
        setLayout(new BorderLayout());

        // --- A. 중앙 제목 영역 (Center) ---
        // 중앙 정렬을 위해 GridBagLayout 사용
        JPanel titleAreaPanel = new JPanel(new GridBagLayout());
        titleAreaPanel.setBackground(new Color(237, 232, 227));

        titleLabel = new JLabel("Git Quiz");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // 중앙 수직/수평 정렬을 위한 가중치 부여
        gbc.anchor = GridBagConstraints.CENTER; // 중앙에 고정

        titleAreaPanel.add(titleLabel, gbc);

        // --- B. 하단 버튼 영역 (South) ---
        // 버튼을 오른쪽으로 배치하기 위해 BorderLayout 사용
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(244, 243, 246));
        footerPanel.setPreferredSize(new Dimension(1, 70)); // 하단 영역 높이 고정 (1px은 최소값)

        startButton = new JButton("시작");
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(116, 116, 117));
        startButton.setContentAreaFilled(false);
        startButton.setOpaque(true);
        startButton.setPreferredSize(new Dimension(120, 40));

        // 버튼 주변에 여백을 주기 위해 패딩 패널 사용
        JPanel buttonPaddingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPaddingPanel.setOpaque(false); // 배경색 투명하게
        buttonPaddingPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 상/좌/하/우 패딩
        buttonPaddingPanel.add(startButton);

        // 버튼을 footerPanel의 오른쪽(EAST)에 배치
        footerPanel.add(buttonPaddingPanel, BorderLayout.EAST);

        // --- C. 최종 조립 ---
        add(titleAreaPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
}