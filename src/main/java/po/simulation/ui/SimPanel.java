package po.simulation.ui;

import po.simulation.Simulation;
import po.simulation.agent.Agent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Główny panel symulacji — zawiera boczny panel statystyk i przycisków
 * sterujących, planszę graficzną oraz dwa wykresy (panika, statystyki wg typu).
 * Odpowiada za logikę wykonywania ticków symulacji i odświeżanie widoku.
 */
public class SimPanel extends JPanel {
    private final Simulation sim;
    private final List<Integer> panicHistory;
    private final Map<String, int[]> typeStats;

    private final JLabel lblTick     = UIHelpers.darkLabel("Tick: 0", "#94a3b8", 11);
    private final JLabel lblBuilding = UIHelpers.darkLabel("0", "#e2e8f0", 22);
    private final JLabel lblInjured  = UIHelpers.darkLabel("0", "#fbbf24", 22);
    private final JLabel lblEvac     = UIHelpers.darkLabel("0", "#4ade80", 22);
    private final JLabel lblDead     = UIHelpers.darkLabel("0", "#f87171", 22);

    private final JButton btnTick = UIHelpers.styledBtn("▶  Tick",     "#f97316", "#fff");
    private final JButton btnAuto = UIHelpers.styledBtn("⟳  Auto-run", "#1e4d2b", "#4ade80");
    private final JButton btnBack = UIHelpers.styledBtn("← Config",    "#1e2736", "#94a3b8");

    private final BoardPanel      boardPanel;
    private final PanicChartPanel panicChart;
    private final BarChartPanel   barChart;

    private boolean isAutoPlaying = false;
    private Thread simThread;

    /**
     * Tworzy panel symulacji.
     *
     * @param sim          symulacja do wyświetlenia i kontrolowania
     * @param panicHistory współdzielona lista historii paniki — do wykresu
     * @param typeStats    współdzielona mapa statystyk wg typu agenta — do wykresu
     * @param onBack       funkcja wywoływana po kliknięciu "Config" — powrót do konfiguracji
     */
    public SimPanel(Simulation sim, List<Integer> panicHistory,
                    Map<String, int[]> typeStats, Runnable onBack) {
        this.sim = sim;
        this.panicHistory = panicHistory;
        this.typeStats = typeStats;

        setBackground(UIColors.BG);
        setLayout(new BorderLayout(0, 0));

        boardPanel = new BoardPanel(sim.getBoard());
        panicChart = new PanicChartPanel(panicHistory);
        barChart   = new BarChartPanel(typeStats);

        add(buildSidebar(onBack), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);

        updateUI();
    }

    /**
     * Buduje boczny panel z przyciskami, statystykami i legendą kolorów.
     *
     * @param onBack funkcja powrotu do konfiguracji
     * @return skonfigurowany panel boczny
     */
    private JPanel buildSidebar(Runnable onBack) {
        JPanel side = new JPanel();
        side.setBackground(UIColors.SIDE);
        side.setPreferredSize(new Dimension(210, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(16, 14, 16, 14));

        JLabel appName = new JLabel("🏢  Ewakuacja");
        appName.setFont(new Font("SansSerif", Font.BOLD, 14));
        appName.setForeground(UIColors.TEXT);
        side.add(appName);
        side.add(Box.createVerticalStrut(4));
        side.add(lblTick);
        side.add(Box.createVerticalStrut(14));

        side.add(UIHelpers.statCard("W budynku",    lblBuilding, "#e2e8f0"));
        side.add(Box.createVerticalStrut(8));
        side.add(UIHelpers.statCard("Rannych",      lblInjured,  "#fbbf24"));
        side.add(Box.createVerticalStrut(8));
        side.add(UIHelpers.statCard("Ewakuowanych", lblEvac,     "#4ade80"));
        side.add(Box.createVerticalStrut(8));
        side.add(UIHelpers.statCard("Zabitych",     lblDead,     "#f87171"));
        side.add(Box.createVerticalStrut(16));

        btnTick.setMaximumSize(new Dimension(9999, 34));
        btnTick.addActionListener(e -> doTick());
        side.add(btnTick);
        side.add(Box.createVerticalStrut(6));

        btnAuto.setMaximumSize(new Dimension(9999, 34));
        btnAuto.addActionListener(e -> { if (isAutoPlaying) stopAuto(); else startAuto(); });
        side.add(btnAuto);
        side.add(Box.createVerticalStrut(6));

        btnBack.setMaximumSize(new Dimension(9999, 34));
        btnBack.addActionListener(e -> { stopAuto(); onBack.run(); });
        side.add(btnBack);

        side.add(Box.createVerticalStrut(16));
        side.add(UIHelpers.legendTitle("Legenda"));
        side.add(Box.createVerticalStrut(6));
        String[][] leg = {{"C – Calm","#1976d2"},{"P – Panicking","#e65100"},
                {"A – Altruist","#8e24aa"},{"I – Injured","#fbc02d"},
                {"S – Firefighter","#00acc1"},{"F – Ogień","#ef5350"},
                {"E – Wyjście","#2e7d32"},{"# – Ściana","#374151"}};
        for (String[] l : leg) {
            side.add(UIHelpers.legendRow(l[0], l[1]));
            side.add(Box.createVerticalStrut(2));
        }
        return side;
    }

    /**
     * Buduje główny obszar widoku — planszę z możliwością przewijania
     * oraz rząd wykresów na dole ekranu.
     *
     * @return skonfigurowany panel głównego obszaru
     */
    private JPanel buildMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout(0, 0));
        mainArea.setBackground(UIColors.BG);

        JScrollPane scroll = new JScrollPane(boardPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIColors.BG);
        scroll.setBackground(UIColors.BG);
        mainArea.add(scroll, BorderLayout.CENTER);

        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 0, 0));
        chartsRow.setPreferredSize(new Dimension(0, 155));
        chartsRow.setBackground(UIColors.BG);
        chartsRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIColors.BORDER));

        JPanel panicWrap = UIHelpers.chartWrap(panicChart, "Średni poziom paniki (ostatnie 60 ticków)");
        JPanel barWrap   = UIHelpers.chartWrap(barChart,   "Ewakuowani / Zabici wg typu agenta");
        panicWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIColors.BORDER));

        chartsRow.add(panicWrap);
        chartsRow.add(barWrap);
        mainArea.add(chartsRow, BorderLayout.SOUTH);
        return mainArea;
    }

    /**
     * Wykonuje jeden tick symulacji, zbiera dane do wykresów
     * (średnia panika, statystyki wg typu) i odświeża interfejs.
     */
    private void doTick() {
        if (!sim.isRunning()) return;
        sim.step();

        List<Agent> agents = sim.getAgents();
        if (!agents.isEmpty()) {
            int sum = agents.stream().mapToInt(Agent::getPanic).sum();
            panicHistory.add(sum / agents.size());
        } else {
            panicHistory.add(0);
        }

        sim.getEvacuatedByType().forEach((t, v) -> {
            int[] s = typeStats.getOrDefault(t, new int[]{0,0});
            s[0] = v; typeStats.put(t, s);
        });
        sim.getDeadByType().forEach((t, v) -> {
            int[] s = typeStats.getOrDefault(t, new int[]{0,0});
            s[1] = v; typeStats.put(t, s);
        });
        updateUI();
    }

    /**
     * Uruchamia automatyczne wykonywanie ticków w osobnym wątku
     * aż do zakończenia symulacji lub kliknięcia "Stop".
     */
    private void startAuto() {
        isAutoPlaying = true;
        btnAuto.setText("■  Stop");
        UIHelpers.styleBtn(btnAuto, "#4d1e1e", "#f87171");
        btnTick.setEnabled(false);
        simThread = new Thread(() -> {
            while (isAutoPlaying && sim.isRunning()) {
                doTick();
                SwingUtilities.invokeLater(this::updateUI);
                try { Thread.sleep(120); } catch (InterruptedException e) { break; }
            }
            SwingUtilities.invokeLater(this::stopAuto);
        });
        simThread.start();
    }

    /** Zatrzymuje automatyczne odtwarzanie i przywraca przycisk do stanu domyślnego. */
    private void stopAuto() {
        isAutoPlaying = false;
        btnAuto.setText("⟳  Auto-run");
        UIHelpers.styleBtn(btnAuto, "#1e4d2b", "#4ade80");
        btnTick.setEnabled(sim.isRunning());
    }

    /**
     * Odświeża wszystkie elementy interfejsu — etykiety statystyk, planszę
     * i wykresy — na podstawie aktualnego stanu symulacji.
     */
    public void updateUI() {
        if (sim == null || sim.getMetrics() == null) return;
        lblTick.setText("Tick: " + sim.getStepCount() + "  ·  " + (sim.isRunning() ? "Running" : "Finished"));
        lblBuilding.setText(String.valueOf(sim.getAliveCount()));
        lblInjured.setText(String.valueOf(sim.getInjuredCount()));
        lblEvac.setText(String.valueOf(sim.getMetrics().getEvacuatedCount()));
        lblDead.setText(String.valueOf(sim.getMetrics().getDeadCount()));
        if (!sim.isRunning()) { btnTick.setEnabled(false); btnAuto.setEnabled(false); }
        boardPanel.repaint();
        panicChart.repaint();
        barChart.repaint();
    }
}