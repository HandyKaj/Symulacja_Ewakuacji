package po.simulation.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Metody pomocnicze do tworzenia i stylizowania komponentów Swing
 * w jednolitym stylu (dark theme) używanym w całej aplikacji.
 */
public class UIHelpers {

    /**
     * Tworzy etykietę tekstową z określonym kolorem i rozmiarem czcionki.
     *
     * @param text  tekst etykiety
     * @param color kolor tekstu w formacie heksadecymalnym
     * @param size  rozmiar czcionki
     * @return skonfigurowana etykieta
     */
    public static JLabel darkLabel(String text, String color, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(UIColors.hex(color));
        l.setFont(new Font("SansSerif", Font.PLAIN, size));
        return l;
    }

    /**
     * Tworzy przycisk ze stylem dark theme.
     *
     * @param text tekst przycisku
     * @param bg   kolor tła
     * @param fg   kolor tekstu
     * @return skonfigurowany przycisk
     */
    public static JButton styledBtn(String text, String bg, String fg) {
        JButton b = new JButton(text);
        styleBtn(b, bg, fg);
        return b;
    }

    /**
     * Stylizuje istniejący przycisk — usuwa domyślne obramowanie,
     * ustawia kolory i kursor wskazujący że element jest klikalny.
     *
     * @param b  przycisk do ostylowania
     * @param bg kolor tła
     * @param fg kolor tekstu
     */
    public static void styleBtn(JButton b, String bg, String fg) {
        b.setBackground(UIColors.hex(bg));
        b.setForeground(UIColors.hex(fg));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }

    /**
     * Stylizuje pole tekstowe — kolory tła, tekstu i obramowania zgodne z dark theme.
     *
     * @param tf pole tekstowe do ostylowania
     */
    public static void styleTextField(JTextField tf) {
        tf.setBackground(UIColors.hex("#0f1117"));
        tf.setForeground(UIColors.hex("#e2e8f0"));
        tf.setCaretColor(UIColors.hex("#f97316"));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.hex("#1e2736"), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    /**
     * Tworzy kartę statystyk wyświetlającą etykietę i wartość liczbową
     * (np. "Ewakuowanych: 24") w bocznym panelu symulacji.
     *
     * @param label    nazwa statystyki
     * @param valLabel etykieta z wartością — aktualizowana z zewnątrz
     * @param valColor kolor wartości
     * @return panel karty statystyk
     */
    public static JPanel statCard(String label, JLabel valLabel, String valColor) {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(9999, 55));

        JLabel lbl = darkLabel(label, "#64748b", 10);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        valLabel.setForeground(UIColors.hex(valColor));
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(valLabel);
        return card;
    }
    /**
     * Owija panel wykresu w kontener z tytułem wyświetlanym nad nim.
     *
     * @param chart panel wykresu do owinięcia
     * @param title tytuł wyświetlany nad wykresem
     * @return panel z tytułem i wykresem
     */
    public static JPanel chartWrap(JPanel chart, String title) {
        JPanel wrap = new JPanel(new BorderLayout(0, 0));
        wrap.setBackground(UIColors.BG);
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(UIColors.hex("#94a3b8"));
        lbl.setBorder(new EmptyBorder(6, 8, 2, 0));
        wrap.add(lbl, BorderLayout.NORTH);
        wrap.add(chart, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Tworzy tytuł sekcji legendy w bocznym panelu.
     *
     * @param text tekst tytułu
     * @return etykieta tytułu legendy
     */
    public static JLabel legendTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(UIColors.hex("#94a3b8"));
        return l;
    }

    /**
     * Tworzy jeden wiersz legendy — kolorowy kwadrat z opisem tekstowym
     * (np. kwadrat niebieski + "C – Calm").
     *
     * @param text  opis tekstowy
     * @param color kolor kwadratu w formacie heksadecymalnym
     * @return panel z jednym wierszem legendy
     */
    public static JPanel legendRow(String text, String color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setBackground(UIColors.SIDE);
        row.setMaximumSize(new Dimension(9999, 16));
        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setBackground(UIColors.hex(color));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(UIColors.hex("#94a3b8"));
        row.add(dot);
        row.add(lbl);
        return row;
    }

    /**
     * Bezpiecznie parsuje tekst na liczbę całkowitą.
     * Używane przy odczycie wartości z pól konfiguracji.
     *
     * @param s tekst do sparsowania
     * @return sparsowana liczba lub 0 jeśli tekst jest niepoprawny
     */
    public static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}