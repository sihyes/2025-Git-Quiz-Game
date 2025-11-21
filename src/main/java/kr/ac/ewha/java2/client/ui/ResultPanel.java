package kr.ac.ewha.java2.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public class ResultPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private MainFrame parentFrame;

    // --- 색상 상수 ---
    private final Color COLOR_BG = new Color(240, 239, 237); // 배경색 (베이지)
    private final Color COLOR_BAR_DEFAULT = new Color(245, 245, 250); // 일반 플레이어 막대 (흰색 계열)
    private final Color COLOR_BAR_ME = new Color(170, 170, 170);      // 내 막대 (진한 회색)
    private final Color COLOR_TEXT_HIGHLIGHT = new Color(230, 100, 50); // 내 순위 텍스트 (주황색)

    // 더미 데이터 (이름, 점수, 본인 여부)
    // 실제로는 서버에서 받아온 데이터를 사용해야 합니다.
    private PlayerData[] players = {
            new PlayerData("player1", 100, false),
            new PlayerData("player2", 60, true), // 나 (rank 3)
            new PlayerData("player3", 75, false),
            new PlayerData("player4", 40, false)
    };

    public ResultPanel(MainFrame parent) {
        this.parentFrame = parent;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // 1. 상단 (헤더 + 텍스트 순위)
        add(createTopSection(), BorderLayout.NORTH);

        // 2. 중앙 (막대 그래프)
        add(createChartSection(), BorderLayout.CENTER);

        // 3. 하단 (나가기 버튼)
        add(createFooterSection(), BorderLayout.SOUTH);
    }

    // ----------------------------------------------------
    // 1. Top Section: Header + Text Ranking
    // ----------------------------------------------------
    private JPanel createTopSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // 1-1. Close Button (Right Aligned) & Title
        // 레이아웃 잡기가 까다로워 간단히 Box 사용
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);
        headerBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // 닫기 버튼
        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if(parentFrame != null) parentFrame.showPanel(MainFrame.LOBBY_SCREEN);
            }
        });

        // 중앙 작은 타이틀
        JLabel subTitle = new JLabel("Git Quiz", SwingConstants.CENTER);
        subTitle.setFont(new Font("Serif", Font.BOLD, 16));

        headerBar.add(subTitle, BorderLayout.CENTER);
        headerBar.add(closeBtn, BorderLayout.EAST);
        panel.add(headerBar);

        // 1-2. Main Title "순위"
        JLabel mainTitle = new JLabel("순위", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Malgun Gothic", Font.PLAIN, 30)); // 한글 폰트
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(20));
        panel.add(mainTitle);

        // 1-3. Text Ranking List
        panel.add(Box.createVerticalStrut(20));

        // 데이터를 점수 내림차순으로 정렬 (순위 계산용)
        PlayerData[] sortedPlayers = players.clone();
        Arrays.sort(sortedPlayers, Comparator.comparingInt(PlayerData::getScore).reversed());

        // 상위 3명만 표시 (또는 전체 표시)
        for (int i = 0; i < sortedPlayers.length; i++) {
            if (i >= 3) break; // 3등까지만 텍스트로 표시

            PlayerData p = sortedPlayers[i];
            int rank = i + 1;

            JLabel rankLabel = new JLabel(String.format("%d : %s - %d", rank, p.name, p.score));
            rankLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // 본인이면 주황색 하이라이트
            if (p.isMe) {
                rankLabel.setForeground(COLOR_TEXT_HIGHLIGHT);
            }

            panel.add(rankLabel);
            panel.add(Box.createVerticalStrut(5));
        }

        return panel;
    }

    // ----------------------------------------------------
    // 2. Chart Section: Bar Graph
    // ----------------------------------------------------
    private JPanel createChartSection() {
        JPanel chartPanel = new JPanel(new GridBagLayout());
        chartPanel.setOpaque(false);
        chartPanel.setBorder(new EmptyBorder(20, 20, 40, 20)); // 하단 여백

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTH; // [중요] 바닥 기준 정렬
        gbc.weighty = 1.0; // 세로 공간 확보
        gbc.weightx = 1.0; // 가로 공간 분배
        gbc.insets = new Insets(0, 10, 0, 10); // 막대 사이 간격

        // 최대 점수 (그래프 높이 비율 계산용)
        int maxScore = 100; // 혹은 Arrays.stream(players).mapToInt(p -> p.score).max().orElse(100);
        int maxBarHeight = 250; // 막대 최대 높이 (픽셀)

        // 플레이어 배열 순서대로 막대 생성 (1, 2, 3, 4)
        for (int i = 0; i < players.length; i++) {
            gbc.gridx = i;
            chartPanel.add(createBarComponent(players[i], maxScore, maxBarHeight), gbc);
        }

        return chartPanel;
    }

    private JPanel createBarComponent(PlayerData p, int maxScore, int maxHeight) {
        // Wrapper Panel (막대 + 텍스트)
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // 1. Bar (JPanel)
        JPanel bar = new JPanel();
        bar.setBackground(p.isMe ? COLOR_BAR_ME : COLOR_BAR_DEFAULT);

        // 높이 계산
        int height = (int) ((double) p.score / maxScore * maxHeight);
        if (height < 10) height = 10; // 최소 높이 보장

        bar.setPreferredSize(new Dimension(100, height)); // 너비 100px 고정

        // 둥근 모서리 효과 (선택사항, 필요 없으면 주석 처리)
        // bar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 2. Label (Player Info)
        JLabel infoLabel = new JLabel(p.name + ": " + p.score, SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setBorder(new EmptyBorder(10, 0, 0, 0)); // 막대와 텍스트 사이 간격

        wrapper.add(bar, BorderLayout.CENTER);
        wrapper.add(infoLabel, BorderLayout.SOUTH);

        return wrapper;
    }

    // ----------------------------------------------------
    // 3. Footer Section: Exit Button
    // ----------------------------------------------------
    private JPanel createFooterSection() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(245, 245, 245)); // 하단 바 배경색 (이미지와 비슷하게)
        footer.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton exitButton = new JButton("나가기");
        exitButton.setPreferredSize(new Dimension(150, 40));
        exitButton.setBackground(new Color(170, 170, 170)); // 버튼 회색
        exitButton.setForeground(Color.BLACK);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        exitButton.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.showPanel(MainFrame.LOBBY_SCREEN);
            }
        });

        footer.add(exitButton);
        return footer;
    }

    // --- Helper Class ---
    private static class PlayerData {
        String name;
        int score;
        boolean isMe;

        public PlayerData(String name, int score, boolean isMe) {
            this.name = name;
            this.score = score;
            this.isMe = isMe;
        }

        public int getScore() { return score; }
    }
}