package po.simulation.ui;

import java.awt.Color;

/**
 * Centralne miejsce definicji kolorów używanych w interfejsie graficznym.
 * Zapewnia spójną paletę kolorów (dark theme) w całej aplikacji.
 */
public class UIColors {
    public static final Color BG     = hex("#0f1117");
    public static final Color SIDE   = hex("#161b26");
    public static final Color CARD   = hex("#0f1117");
    public static final Color BORDER = hex("#1e2736");
    public static final Color TEXT   = hex("#e2e8f0");
    public static final Color MUTED  = hex("#64748b");
    public static final Color ORANGE = hex("#f97316");
    public static final Color GREEN  = hex("#4ade80");
    public static final Color YELLOW = hex("#fbbf24");
    public static final Color RED    = hex("#f87171");

    /**
     * Konwertuje kod heksadecymalny koloru na obiekt Color.
     *
     * @param h kod koloru w formacie "#RRGGBB"
     * @return obiekt Color
     */
    public static Color hex(String h) { return Color.decode(h); }

    /**
     * Zwraca kolor przypisany do danego typu agenta — używany do rysowania
     * agentów na planszy w BoardPanel.
     *
     * @param type nazwa klasy agenta (np. "Calm", "Panicking")
     * @return kolor odpowiadający typowi agenta
     */
    public static Color agentColor(String type) {
        return switch (type) {
            case "Calm"        -> hex("#1976d2");
            case "Panicking"   -> hex("#e65100");
            case "Altruist"    -> hex("#8e24aa");
            case "Injured"     -> hex("#fbc02d");
            case "Firefighter" -> hex("#00acc1");
            default            -> hex("#ffffff");
        };
    }
}
