package po.simulation.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class ConfigPanel extends JPanel {
    private final JTextField txtCalm        = new JTextField("40", 8);
    private final JTextField txtPanic       = new JTextField("20", 8);
    private final JTextField txtAltruist    = new JTextField("10", 8);
    private final JTextField txtInjured     = new JTextField("6",  8);
    private final JTextField txtFirefighter = new JTextField("8",  8);

    public ConfigPanel(Consumer<int[]> onStart) {
        setBackground(UIColors.BG);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(UIColors.SIDE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER, 1),
                new EmptyBorder(32, 40, 32, 40)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Konfiguracja symulacji");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(UIColors.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        JLabel sub = new JLabel("Ustaw liczbę agentów każdego typu");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(UIColors.MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        addRow(card, "Spokojny (Calm)",        txtCalm,        "#1976d2");
        addRow(card, "Panikujący (Panicking)", txtPanic,       "#e65100");
        addRow(card, "Altruista (Altruist)",   txtAltruist,    "#8e24aa");
        addRow(card, "Ranny (Injured)",        txtInjured,     "#fbc02d");
        addRow(card, "Strażak (Firefighter)",  txtFirefighter, "#00acc1");

        card.add(Box.createVerticalStrut(24));

        JButton btnStart = UIHelpers.styledBtn("Uruchom symulację", "#f97316", "#fff");
        btnStart.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.setPreferredSize(new Dimension(240, 42));
        btnStart.setMaximumSize(new Dimension(240, 42));
        btnStart.addActionListener(e -> onStart.accept(new int[]{
                UIHelpers.parseInt(txtCalm.getText()),
                UIHelpers.parseInt(txtPanic.getText()),
                UIHelpers.parseInt(txtAltruist.getText()),
                UIHelpers.parseInt(txtInjured.getText()),
                UIHelpers.parseInt(txtFirefighter.getText())
        }));
        card.add(btnStart);
        add(card);
    }

    private void addRow(JPanel p, String label, JTextField tf, String dotColor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        row.setBackground(UIColors.SIDE);
        row.setMaximumSize(new Dimension(400, 36));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setBackground(UIColors.hex(dotColor));

        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(200, 28));
        lbl.setForeground(UIColors.TEXT);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));

        UIHelpers.styleTextField(tf);
        tf.setPreferredSize(new Dimension(60, 28));

        row.add(dot); row.add(lbl); row.add(tf);
        p.add(row);
    }
}