package po.simulation;

import po.simulation.Simulation;
import po.simulation.agent.AgentFactory;
import po.simulation.agent.Agent;
import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.config.SimConfig;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;
import po.simulation.model.AgentState;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Random;

public class Main {
    private final JFrame frame = new JFrame("Symulacja Ewakuacji Pożarowej (Swing)");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // Config data
    private final JTextField txtCalm = new JTextField("40", 10);
    private final JTextField txtPanic = new JTextField("20", 10);
    private final JTextField txtAltruist = new JTextField("10", 10);
    private final JTextField txtInjured = new JTextField("6", 10);
    private final JTextField txtFirefighters = new JTextField("8", 10);

    // Simulation state
    private Simulation simulationInstance = null;
    private boolean isAutoPlaying = false;
    private Thread simThread = null;

    // Config panel settings
    private BoardPanel boardPanel = null;
    private final JLabel lblStep = new JLabel("Current Step: 0");
    private final JLabel lblStatus = new JLabel("Status: Ready");
    private final JButton btnStep = new JButton("Do 1 move (Tick)");
    private final JButton btnAuto = new JButton("Start Auto-run");

    // Etykiety statystyk w lewym panelu bocznym
    private final JLabel lblInBuilding = new JLabel("W budynku: 0");
    private final JLabel lblInjured = new JLabel("Rannych: 0");
    private final JLabel lblEvacuated = new JLabel("Ewakuowanych: 0");
    private final JLabel lblKilled = new JLabel("Zabitych: 0");

    private final List<Integer> panicHistory = new ArrayList<>();
    private final Map<String, int[]> classStats = new HashMap<>(); // Klasa -> [Uciekło, Umarło]
    private BarChartPanel barChartPanel;

    public Main() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLocationRelativeTo(null);

        setupConfigPanel();
        setupSimulationPanelPlaceholder();

        frame.add(mainPanel);
        cardLayout.show(mainPanel, "CONFIG");

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isAutoPlaying = false;
                super.windowClosing(e);
            }
        });
    }

    private void initClassStats() {
        classStats.clear();
        classStats.put("Calm", new int[]{0, 0});
        classStats.put("Panicking", new int[]{0, 0});
        classStats.put("Altruist", new int[]{0, 0});
        classStats.put("Injured", new int[]{0, 0});
        classStats.put("Firefighter", new int[]{0, 0});
    }

    public void show() {
        frame.setVisible(true);
    }

    private void setupConfigPanel() {

        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBorder(new EmptyBorder(32, 32, 32, 32));

        JLabel lblTitle = new JLabel("Config");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        configPanel.add(lblTitle);
        configPanel.add(Box.createVerticalStrut(20));

        addConfigRow(configPanel, "Number of Calm agents", txtCalm);
        addConfigRow(configPanel, "Number of Panicking agents", txtPanic);
        addConfigRow(configPanel, "Number of Altruist agents", txtAltruist);
        addConfigRow(configPanel, "Number of Injured agents", txtInjured);
        addConfigRow(configPanel, "Number of Firefighters type agents", txtFirefighters);

        configPanel.add(Box.createVerticalStrut(20));

        JButton btnInit = new JButton("Start simulation");
        btnInit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnInit.setPreferredSize(new Dimension(200, 40));
        btnInit.setMaximumSize(new Dimension(200, 40));
        btnInit.addActionListener(e -> {
            Integer calm = tryParseInt(txtCalm.getText());
            Integer panic = tryParseInt(txtPanic.getText());
            Integer altruist = tryParseInt(txtAltruist.getText());
            Integer injured = tryParseInt(txtInjured.getText());
            Integer firefighters = tryParseInt(txtFirefighters.getText());

            simulationInstance = startAndSetupSimulationSwing(calm, panic, altruist, injured, firefighters);
            initClassStats();
            rebuildSimulationPanel();
            cardLayout.show(mainPanel, "SIMULATION");
        });
        configPanel.add(btnInit);

        mainPanel.add(configPanel, "CONFIG");
    }

    private void addConfigRow(JPanel panel, String labelText, JTextField textField) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(220, 25));
        row.add(label);
        row.add(textField);
        panel.add(row);
    }

    private void setupSimulationPanelPlaceholder() {
        mainPanel.add(new JPanel(), "SIMULATION");
    }

    private void rebuildSimulationPanel() {
        Simulation sim = simulationInstance;
        if (sim == null) return;

        JPanel simPanel = new JPanel(new BorderLayout(16, 16));
        simPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(320, 800));

        JLabel lblControlTitle = new JLabel("Control Panel");
        lblControlTitle.setFont(new Font("Arial", Font.BOLD, 18));
        controlPanel.add(lblControlTitle);
        controlPanel.add(Box.createVerticalStrut(12));

        lblStep.setText("Current Step: " + sim.getStepCount());
        controlPanel.add(lblStep);
        controlPanel.add(Box.createVerticalStrut(12));

        lblStatus.setText("Status: " + (sim.isRunning() ? "Running" : "Finished"));
        controlPanel.add(lblStatus);
        controlPanel.add(Box.createVerticalStrut(12));

        btnStep.setText("Do 1 move (Tick)");
        btnStep.setMaximumSize(new Dimension(240, 35));
        btnStep.setEnabled(sim.isRunning() && !isAutoPlaying);

        for (java.awt.event.ActionListener al : btnStep.getActionListeners()) {
            btnStep.removeActionListener(al);
        }
        btnStep.addActionListener(e -> {
            sim.step();
            updateUIState();
        });
        controlPanel.add(btnStep);
        controlPanel.add(Box.createVerticalStrut(12));

        btnAuto.setText("Start Auto-run");
        btnAuto.setMaximumSize(new Dimension(240, 35));
        btnAuto.setEnabled(sim.isRunning());

        for (java.awt.event.ActionListener al : btnAuto.getActionListeners()) {
            btnAuto.removeActionListener(al);
        }
        btnAuto.addActionListener(e -> {
            if (isAutoPlaying) {
                stopAutoRun();
            } else {
                startAutoRun();
            }
        });
        controlPanel.add(btnAuto);
        controlPanel.add(Box.createVerticalStrut(12));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(240, 2));
        controlPanel.add(separator);
        controlPanel.add(Box.createVerticalStrut(12));

        JButton btnBack = new JButton("Back to configuration");
        btnBack.setMaximumSize(new Dimension(240, 35));
        btnBack.setBackground(new Color(0xFFCCCCCC));
        btnBack.addActionListener(e -> {
            stopAutoRun();
            cardLayout.show(mainPanel, "CONFIG");
        });
        controlPanel.add(btnBack);
        controlPanel.add(Box.createVerticalStrut(20));

        JLabel statsTitle = new JLabel("Aktualne statystyki:");
        statsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(statsTitle);
        controlPanel.add(Box.createVerticalStrut(8));

        lblInBuilding.setFont(new Font("Arial", Font.PLAIN, 13));
        lblInjured.setFont(new Font("Arial", Font.PLAIN, 13));
        lblEvacuated.setFont(new Font("Arial", Font.PLAIN, 13));
        lblKilled.setFont(new Font("Arial", Font.PLAIN, 13));

        controlPanel.add(lblInBuilding);
        controlPanel.add(Box.createVerticalStrut(4));
        controlPanel.add(lblInjured);
        controlPanel.add(Box.createVerticalStrut(4));
        controlPanel.add(lblEvacuated);
        controlPanel.add(Box.createVerticalStrut(4));
        controlPanel.add(lblKilled);
        controlPanel.add(Box.createVerticalStrut(20));

        barChartPanel = new BarChartPanel();
        controlPanel.add(barChartPanel);
        controlPanel.add(Box.createVerticalStrut(10));

        simPanel.add(controlPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel lblBoardTitle = new JLabel("Burning Building Visualization", SwingConstants.CENTER);
        lblBoardTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblBoardTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        rightPanel.add(lblBoardTitle, BorderLayout.NORTH);

        boardPanel = new BoardPanel(sim.getBoard());
        rightPanel.add(boardPanel, BorderLayout.CENTER);
        simPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.remove(mainPanel.getComponent(1));
        mainPanel.add(simPanel, "SIMULATION");
        mainPanel.revalidate();
        mainPanel.repaint();
        updateUIState();
    }

    private void startAutoRun() {
        Simulation sim = simulationInstance;
        if (sim == null) return;
        isAutoPlaying = true;
        btnAuto.setText("Stop Auto-run");
        btnStep.setEnabled(false);
        simThread = new Thread(() -> {
            while (isAutoPlaying && sim.isRunning()) {
                sim.step();
                SwingUtilities.invokeLater(this::updateUIState);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (!sim.isRunning()) {
                SwingUtilities.invokeLater(this::stopAutoRun);
            }
        });
        simThread.start();
    }

    private void stopAutoRun() {
        isAutoPlaying = false;
        btnAuto.setText("Start Auto-run");
        btnStep.setEnabled(simulationInstance != null && simulationInstance.isRunning());
        lblStatus.setText("Status: " + (simulationInstance != null && simulationInstance.isRunning() ? "Running" : "Finished"));
    }

    private void updateUIState() {
        Simulation sim = simulationInstance;
        if (sim == null) return;

        lblStep.setText("Current Step: " + sim.getStepCount());
        lblStatus.setText("Status: " + (sim.isRunning() ? "Running" : "Finished"));

        lblInBuilding.setText("W budynku: " + sim.getAliveCount());
        lblInjured.setText("Rannych: " + sim.getInjuredCount());
        lblEvacuated.setText("Ewakuowanych: " + sim.getMetrics().getEvacuatedCount());
        lblKilled.setText("Zabitych: " + sim.getMetrics().getDeadCount());
        classStats.clear();

        String[] classes = {
                "Calm",
                "Panicking",
                "Altruist",
                "Injured",
                "Firefighter"
        };

        for (String type : classes) {

            int evacuated = sim.getEvacuatedByType().getOrDefault(type, 0);

            int dead = sim.getDeadByType().getOrDefault(type, 0);

            classStats.put(type, new int[]{evacuated, dead});
        }


        if (!sim.isRunning()) {
            btnStep.setEnabled(false);
            btnAuto.setEnabled(false);
        }
        if (boardPanel != null) {
            boardPanel.repaint();
        }
        if (barChartPanel != null) {
            barChartPanel.repaint();
        }
    }

    private Integer tryParseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Simulation startAndSetupSimulationSwing(int calm, int panic, int altruist, int injured, int firefighters) {
        Random random = new Random();
        SimConfig config = new SimConfig(0.3f, 0.3f, 3, 10);

        Simulation sim = new Simulation(200, 100, config);
        sim.initialize();
        Board board = sim.getBoard();
        int width = board.getWidth();
        int height = board.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board.getCell(x, y).setType(CellType.CORRIDOR);
            }
        }

        for (int x = 0; x < width; x++) {
            board.getCell(x, 0).setType(CellType.WALL);
            if (x < 156 || x > 164) {
                board.getCell(x, height - 1).setType(CellType.WALL);
            }
        }

        for (int y = 0; y < height; y++) {
            board.getCell(0, y).setType(CellType.WALL);
            board.getCell(width - 1, y).setType(CellType.WALL);
        }

        for (int x = 1; x < 40; x++) {
            board.getCell(x, 50).setType(CellType.WALL);
        }

        for (int y = 1; y < (height - 1); y++) {
            if ((y < 24 || y > 26) && (y < 74 || y > 76)) {
                board.getCell(40, y).setType(CellType.WALL);
            }
        }

        for (int y = 1; y < (height - 1); y++) {
            if (y < 45 || y > 55) {
                board.getCell(60, y).setType(CellType.WALL);
            }
        }

        for (int x = 60; x < 120; x++) {
            if (x < 90 || x > 100) {
                board.getCell(x, 55).setType(CellType.WALL);
                board.getCell(x, 45).setType(CellType.WALL);
            }
        }

        for (int y = 1; y < 45; y++) {
            if (y < 22 || y > 24) {
                board.getCell(80, y).setType(CellType.WALL);
            }
        }

        for (int y = 1; y < (height - 1); y++) {
            if ((y < 12 || y > 14) && (y < 34 || y > 36)) {
                board.getCell(120, y).setType(CellType.WALL);
            }
        }

        for (int x = 156; x <= 164; x++) {
            for (int y = 96; y < height; y++) {
                board.getCell(x, y).setType(CellType.EXIT);
            }
        }

        for (int x = 158; x <= 162; x++) {
            board.getCell(x, height - 1).setType(CellType.EXIT);
        }

        int x = random.nextInt(98) + 1;
        int y = random.nextInt(98) + 1;

        board.getCell(x, y).setFire(new Fire(80));





        AgentFactory factory = AgentFactory.getInstance();
        int currentId = 1;

        currentId = spawnAgentsRandomlySwing(sim, board, factory, "CALM", calm, currentId, random);
        currentId = spawnAgentsRandomlySwing(sim, board, factory, "PANICKING", panic, currentId, random);
        currentId = spawnAgentsRandomlySwing(sim, board, factory, "ALTRUIST", altruist, currentId, random);
        currentId = spawnAgentsRandomlySwing(sim, board, factory, "INJURED", injured, currentId, random);
        spawnAgentsRandomlySwing(sim, board, factory, "FIREFIGHTER", firefighters, currentId, random);

        return sim;
    }

    public static int spawnAgentsRandomlySwing(
            Simulation sim, Board board, AgentFactory factory,
            String type, int count, int startId, Random random
    ) {
        int id = startId;
        int maxAttempts = 1000;
        for (int i = 0; i < count; i++) {
            boolean spawned = false;
            int attempts = 0;

            while (!spawned && attempts < maxAttempts) {
                attempts++;
                int rx = random.nextInt(board.getWidth());
                int ry = random.nextInt(board.getHeight());
                Cell targetCell = board.getCell(rx, ry);
                if (targetCell != null && targetCell.isEmpty() && !targetCell.hasFire() &&
                        targetCell.getType() != CellType.WALL && targetCell.getType() != CellType.EXIT
                ) {
                    Agent agent = factory.createAgent(type, id, type, board, rx, ry);
                    sim.addAgent(agent);
                    id++;
                    spawned = true;
                }
            }
            if (attempts >= maxAttempts) {
                break;
            }
        }
        return id;
    }

    private static class BoardPanel extends JPanel {
        private final Board board;
        private final int cellSize = 7;

        public BoardPanel(Board board) {
            this.board = board;
            int w = board.getWidth() * cellSize + (board.getWidth() - 1);
            int h = board.getHeight() * cellSize + (board.getHeight() - 1);
            setPreferredSize(new Dimension(w, h));
            setBackground(Color.LIGHT_GRAY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 6));
            for (int y = 0; y < board.getHeight(); y++) {
                for (int x = 0; x < board.getWidth(); x++) {
                    Cell cell = board.getCell(x, y);
                    if (cell == null) continue;

                    Color color;
                    if (cell.getType() == CellType.WALL) {
                        color = Color.DARK_GRAY;
                    } else if (cell.getType() == CellType.EXIT) {
                        color = new Color(0xFF2E7D32);
                    } else if (cell.hasFire()) {
                        if (cell.getFire().getIntensity() > 70) {
                            color = new Color(0xFFD32F2F);
                        } else {
                            color = new Color(0xFFFF9800);
                        }
                    } else if (cell.getAgent() != null) {
                        String agentType = cell.getAgent().getClass().getSimpleName();
                        switch (agentType) {
                            case "Calm": color = new Color(0xFF1976D2); break;
                            case "Panicking": color = new Color(0xFFE65100); break;
                            case "Altruist": color = new Color(0xFF8E24AA); break;
                            case "Injured": color = new Color(0xFFFBC02D); break;
                            case "Firefighter": color = new Color(0xFF00ACC1); break;
                            default: color = Color.BLUE; break;
                        }
                    } else {
                        color = Color.WHITE;
                    }

                    int px = x * (cellSize + 1);
                    int py = y * (cellSize + 1);

                    g2d.setColor(color);
                    g2d.fillRect(px, py, cellSize, cellSize);
                    if (cell.getAgent() != null) {
                        String letter = cell.getAgent().getClass().getSimpleName().substring(0, 1);
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(letter, px + 1, py + cellSize - 1);
                    }
                }
            }
        }
    }

    private class BarChartPanel extends JPanel {
        public BarChartPanel() {
            setPreferredSize(new Dimension(500, 160));
            setMaximumSize(new Dimension(500, 160));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            String[] classes = {"Calm", "Panicking", "Altruist", "Injured", "Firefighter"};
            int x = 15;

            for (String c : classes) {
                int[] stats = classStats.getOrDefault(c, new int[]{0, 0});
                int escaped = stats[0];
                int dead = stats[1];

                g2.setColor(new Color(0xFF2E7D32));
                g2.fillRect(x, 120 - escaped * 4, 15, escaped * 4);

                g2.setColor(new Color(0xFFD32F2F));
                g2.fillRect(x + 18, 120 - dead * 4, 15, dead * 4);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.drawString(c.substring(0, Math.min(c.length(), 4)), x, 135);
                x += 55;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().show();
        });
    }
}