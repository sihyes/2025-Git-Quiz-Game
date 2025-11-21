package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoomPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private MainFrame parentFrame;
    private JPanel roomGridPanel;

    // [수정 1] 데이터 구조 변경: { "방 이름", new String[]{"이메일1", "이메일2"...} }
    // 최대 4명이라고 가정하고 데이터를 구성했습니다.
    private static final Object[][] DUMMY_ROOM_DATA = {
            { "Team1", new String[]{"player1@naver.com"} },
            { "Team2", new String[]{"user_a@gmail.com"} },
            { "Team3", new String[]{} }, // 아무도 없는 방
            { "Team4", new String[]{"full@test.com"} }
    };

    // 한 방의 최대 인원 (슬롯 개수)
    private static final int MAX_PLAYERS = 4;

    public RoomPanel() {
        initialize();
    }

    public RoomPanel(MainFrame parent) {
        this();
        this.parentFrame = parent;
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        roomGridPanel = createRoomGridPanel(DUMMY_ROOM_DATA);
        add(roomGridPanel, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JLabel playerLabel = new JLabel("  👤 player1");
        playerLabel.setFont(new Font("Serif", Font.BOLD, 18));

        JPanel playerWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        playerWrap.setOpaque(false);
        playerWrap.add(playerLabel);
        playerWrap.setPreferredSize(new Dimension(150, 40));

        headerPanel.add(playerWrap, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Git Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel logOutWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logOutWrap.setOpaque(false);
        JButton logOutButton = new JButton("LOG OUT");
        logOutButton.setPreferredSize(new Dimension(90, 25));
        logOutWrap.add(logOutButton);

        logOutButton.addActionListener(e -> {
            if (parentFrame != null) parentFrame.showPanel(MainFrame.TITLE_SCREEN);
        });

        headerPanel.add(logOutWrap, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createRoomGridPanel(Object[][] roomData) {
        // 카드 배치를 위한 GridBagLayout
        GridBagLayout gbl = new GridBagLayout();
        gbl.rowWeights = new double[]{1.0};
        gbl.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0};

        JPanel gridPanel = new JPanel(gbl);
        gridPanel.setOpaque(false); // 배경 투명하게

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 5, 0, 5); // 카드 사이 간격

        for (int i = 0; i < roomData.length; i++) {
            gbc.gridx = i;
            gbc.weightx = 1.0;
            JPanel roomContainer = createRoomContainer(roomData[i]);
            gridPanel.add(roomContainer, gbc);
        }
        return gridPanel;
    }

    private JPanel createRoomContainer(Object[] data) {
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);

        JPanel card = createRoomCard(data);
        container.add(card, BorderLayout.CENTER);

        // 방 이름 (데이터의 0번째 인덱스)
        JLabel roomName = new JLabel((String)data[0], SwingConstants.CENTER);
        roomName.setFont(new Font("Serif", Font.BOLD, 14));
        container.add(roomName, BorderLayout.SOUTH);

        return container;
    }

    // [핵심 수정] 카드 내부를 플레이어 목록으로 채우는 메서드
    private JPanel createRoomCard(Object[] data) {
        String[] players = (String[]) data[1]; // 데이터의 1번째는 이메일 배열
        int currentCount = players.length;

        // GridLayout(4행, 1열)을 사용하여 4줄을 균등하게 배치
        JPanel card = new JPanel(new GridLayout(MAX_PLAYERS, 1));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setBackground(Color.WHITE);

        // 인원이 꽉 찼거나(4명) 2명 이상일 때 배경색 변경 (원하는 대로 로직 수정 가능)
        if (currentCount >= 2) {
            card.setBackground(new Color(230, 230, 230)); // 회색 배경
        }

        // 클릭 이벤트
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "방 [" + data[0] + "] 입장 요청", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // [수정 2] 최대 인원(4명)만큼 반복하며 라벨 생성
        for (int i = 0; i < MAX_PLAYERS; i++) {
            String text = "..."; // 기본값

            // 현재 인원이 있으면 해당 이메일 표시
            if (i < players.length) {
                text = players[i];
            }

            JLabel userLabel = new JLabel(text, SwingConstants.CENTER);
            userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); // 폰트 크기 조정

            // 이메일이면 검정색, 빈칸(...)이면 연한 회색으로 글자색 구분
            if (text.equals("...")) {
                userLabel.setForeground(Color.GRAY);
            } else {
                userLabel.setForeground(Color.BLACK);
            }

            card.add(userLabel);
        }

        return card;
    }

    // ----------------------------------------------------
    // --- Footer Panel 생성 메서드 (수정됨) ---
    // ----------------------------------------------------
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footerPanel.setOpaque(false);

        JButton configButton = new JButton("방 설정");
        JButton startGameButton = new JButton("게임 시작");

        configButton.setPreferredSize(new Dimension(100, 35));
        startGameButton.setPreferredSize(new Dimension(100, 35));

        configButton.setBackground(new Color(190, 190, 190));
        startGameButton.setBackground(new Color(190, 190, 190));
        configButton.setOpaque(true);
        startGameButton.setOpaque(true);

        // 방 설정 버튼 클릭 -> 설정 화면으로 이동
        configButton.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.showPanel(MainFrame.ROOM_SETTING_SCREEN);
            }
        });

        // [수정] 게임 시작 버튼 클릭 -> 퀴즈 화면(QuizPanel)으로 이동
        startGameButton.addActionListener(e -> {
            if (parentFrame != null) {
                // "Quiz"라는 이름으로 등록된 패널을 보여줌
                parentFrame.showPanel(MainFrame.QUIZ_SCREEN);
            }
        });

        footerPanel.add(configButton);
        footerPanel.add(startGameButton);

        return footerPanel;
    }
}
