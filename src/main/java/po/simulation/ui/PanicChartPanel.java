package po.simulation.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel rysujący wykres liniowy średniego poziomu paniki agentów
 * w czasie — ostatnie 60 ticków symulacji.
 */
public class PanicChartPanel extends JPanel {
    private final List<Integer> panicHistory;

    /**
     * Tworzy panel wykresu połączony z historią danych paniki.
     *
     * @param panicHistory lista wartości średniej paniki w kolejnych tickach
     */
    public PanicChartPanel(List<Integer> panicHistory) {
        this.panicHistory = panicHistory;
        setBackground(UIColors.BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        int pl=36, pr=12, pt=8, pb=20;
        int w = W-pl-pr, h = H-pt-pb;

        // we only display the last 60 points to make the chart readable
        List<Integer> data = panicHistory.size() > 60
                ? panicHistory.subList(panicHistory.size()-60, panicHistory.size())
                : panicHistory;

        if (data.isEmpty()) {
            g2.setColor(UIColors.hex("#475569"));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("Brak danych", pl+w/2-30, pt+h/2);
            return;
        }

        g2.setColor(UIColors.hex("#1e2736")); g2.setStroke(new BasicStroke(0.5f));
        for (int v : new int[]{0,25,50,75,100}) {
            int vy = pt+h - (int)((v/100.0)*h);
            g2.drawLine(pl, vy, pl+w, vy);
            g2.setColor(UIColors.hex("#475569"));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.drawString(String.valueOf(v), pl-26, vy+3);
            g2.setColor(UIColors.hex("#1e2736"));
        }

        int n = data.size();
        float[] xs = new float[n], ys = new float[n];
        for (int i=0;i<n;i++) {
            xs[i] = pl + (n==1 ? w/2f : (float)i/(n-1)*w);
            ys[i] = pt + h - (data.get(i)/100f)*h;
        }

        Polygon area = new Polygon();
        for (int i=0;i<n;i++) area.addPoint((int)xs[i],(int)ys[i]);
        area.addPoint((int)xs[n-1], pt+h); area.addPoint((int)xs[0], pt+h);
        g2.setColor(new Color(249,115,22,40)); g2.fillPolygon(area);

        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(UIColors.ORANGE);
        for (int i=1;i<n;i++) g2.drawLine((int)xs[i-1],(int)ys[i-1],(int)xs[i],(int)ys[i]);

        g2.setColor(UIColors.hex("#475569"));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        int step = Math.max(1, n/6);
        for (int i=0;i<n;i+=step) {
            int tick = panicHistory.size()-n+i;
            g2.drawString(String.valueOf(tick),(int)xs[i]-6, pt+h+pb-4);
        }
    }
}