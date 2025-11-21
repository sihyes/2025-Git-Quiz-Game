package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LobbyPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private MainFrame parentFrame;

    // [중요] 버튼을 멤버 변수로 선언하여 어디서든 안전하게 접근 가능하게 변경
    private JButton logOutButton;
    private JButton enterRoomButton;
    private JButton createRoomButton;

    // 서버에서 받아올 더미 데이터 (문항수, 시간, 인원, 방이름)
    private static final Object[][] DUMMY_ROOM_DATA = {
            {15, 10, 1, "Room1"},
            {20, 10, 2, "Room2"},
            {10, 30, 3, "Room3"},
            {15, 20, 4, "Room4"}
    };

    // --- 1. 생성자 ---
    public LobbyPanel() {
        initialize();
    }

    // LobbyPanel.java 의 생성자 부분만 수정하면 됩니다.

    public LobbyPanel(MainFrame parent) {
        this.parentFrame = parent;

        initialize();

        if (parentFrame != null) {
            // 로그아웃 -> 타이틀 화면
            logOutButton.addActionListener(e -> parentFrame.showPanel(MainFrame.TITLE_SCREEN));

            // [수정된 부분] 방 입장 버튼 -> RoomPanel(ROOM_SCREEN)로 이동
            enterRoomButton.addActionListener(e -> {
                // 필요하다면 여기서 선택된 방의 정보를 넘겨주는 로직 추가 가능
                parentFrame.showPanel(MainFrame.ROOM_SCREEN);
            });

            // 방 생성 버튼 -> 방 설정 화면(ROOM_SETTING_SCREEN)으로 이동
            createRoomButton.addActionListener(e -> parentFrame.showPanel(MainFrame.ROOM_SETTING_SCREEN));
        }
    }

    // --- 2. UI 초기화 및 레이아웃 설정 ---
    private void initialize() {
        // 루트 레이아웃: BorderLayout으로 Header, Grid, Footer 분리
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 2-1. [NORTH] Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2-2. [CENTER] Room Grid Panel (중간 방 목록)
        add(createRoomGridPanel(DUMMY_ROOM_DATA), BorderLayout.CENTER);

        // 2-3. [SOUTH] Footer Panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // ----------------------------------------------------
    // --- Header Panel 생성 메서드 ---
    // ----------------------------------------------------
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // A. Left: Player Info
        JLabel playerLabel = new JLabel("  👤 player1");
        playerLabel.setFont(new Font("Serif", Font.BOLD, 18));
        headerPanel.add(playerLabel, BorderLayout.WEST);

        // B. Center: Git Quiz Title
        JLabel titleLabel = new JLabel("Git Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // C. Right: Log Out Button
        JPanel logOutWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logOutWrap.setOpaque(false);

        // 멤버 변수에 할당
        logOutButton = new JButton("LOG OUT");
        logOutButton.setPreferredSize(new Dimension(90, 25));

        logOutWrap.add(logOutButton);
        headerPanel.add(logOutWrap, BorderLayout.EAST);

        return headerPanel;
    }

    // ----------------------------------------------------
    // --- Room Grid Panel 생성 메서드 ---
    // ----------------------------------------------------
    private JPanel createRoomGridPanel(Object[][] roomData) {
        // 방 목록을 담을 패널 생성
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // 전체 공간 채움
        gbc.weighty = 1.0; // 세로 확장
        gbc.insets = new Insets(5, 5, 5, 5); // 카드 간 여백

        for (int i = 0; i < roomData.length; i++) {
            gbc.gridx = i;
            gbc.weightx = 1.0; // 가로 공간 균등 분배

            // 방 정보와 이름을 포함하는 카드 컨테이너 생성
            JPanel roomContainer = createRoomContainer(roomData[i]);
            gridPanel.add(roomContainer, gbc);
        }

        // 데이터가 적을 때 왼쪽 정렬을 유지하기 위한 빈 공간 채우기용 더미 (선택 사항)
        // 현재는 weightx가 있어서 꽉 차게 나옵니다.

        return gridPanel;
    }

    // ----------------------------------------------------
    // --- 개별 Room Card 컨테이너 생성 메서드 ---
    // ----------------------------------------------------
    private JPanel createRoomContainer(Object[] data) {
        // Container: 카드와 방 이름을 수직으로 쌓기
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);

        // A. Room Card (클릭 가능한 영역)
        JPanel card = createRoomCard(data);
        container.add(card, BorderLayout.CENTER);

        // B. Room Name (하단 중앙 정렬)
        JLabel roomName = new JLabel((String)data[3], SwingConstants.CENTER);
        roomName.setFont(new Font("Serif", Font.BOLD, 14));
        container.add(roomName, BorderLayout.SOUTH);

        return container;
    }

    // --- Room Card (클릭 가능한 디자인) 생성 메서드 ---
    private JPanel createRoomCard(Object[] data) {
        // Card 디자인: GridBagLayout으로 내부 텍스트를 정렬
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setBackground(Color.WHITE);

        // 인원이 2명 이상인 방은 색상을 다르게 표시
        if ((int)data[2] > 1) {
            card.setBackground(new Color(220, 220, 220));
        }

        // 마우스 클릭 이벤트
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "방 [" + data[3] + "]에 입장 요청", "방 입장", JOptionPane.INFORMATION_MESSAGE);
                // 실제 구현 시: parentFrame.enterRoom(data[3]); 등의 로직 호출
            }
        });

        // 내부 텍스트 라벨 추가
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 0; card.add(new JLabel("문항 개수 " + data[0]), gbc);
        gbc.gridy = 1; card.add(new JLabel("제한 시간 " + data[1]), gbc);
        gbc.gridy = 2; card.add(new JLabel("현재 인원 " + data[2]), gbc);

        return card;
    }

    // ----------------------------------------------------
    // --- Footer Panel 생성 메서드 ---
    // ----------------------------------------------------
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footerPanel.setOpaque(false);

        // 멤버 변수에 할당
        enterRoomButton = new JButton("방 입장");
        createRoomButton = new JButton("방 생성");

        enterRoomButton.setPreferredSize(new Dimension(100, 35));
        createRoomButton.setPreferredSize(new Dimension(100, 35));

        // 스타일 적용
        enterRoomButton.setBackground(new Color(190, 190, 190));
        createRoomButton.setBackground(new Color(190, 190, 190));
        enterRoomButton.setOpaque(true); // Mac 등 일부 OS 호환성
        createRoomButton.setOpaque(true);
        enterRoomButton.setBorderPainted(true); // 버튼 테두리 보이게

        footerPanel.add(enterRoomButton);
        footerPanel.add(createRoomButton);

        return footerPanel;
    }
}