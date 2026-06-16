package po.simulation.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private final Map<String, int[]> typeStats;

    public BarChartPanel(Map<String, int[]> typeStats) {
        this.typeStats = typeStats;
        setBackground(UIColors.BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W=getWidth(), H=getHeight();
        int pl=8, pr=8, pt=8, pb=22;
        int w=W-pl-pr, h=H-pt-pb;

        String[] types  = {"Calm","Panicking","Altruist","Injured","Firefighter"};
        String[] labels = {"Calm","Panic","Altr.","Injur.","FF"};
        int maxVal = 1;
        for (String t : types) {
            int[] s = typeStats.getOrDefault(t, new int[]{0,0});
            maxVal = Math.max(maxVal, Math.max(s[0], s[1]));
        }

        int n = types.length;
        float groupW = (float)w/n;
        float barW = groupW*0.28f;

        g2.setColor(UIColors.hex("#1e2736")); g2.setStroke(new BasicStroke(0.5f));
        g2.drawLine(pl, pt+h, pl+w, pt+h);

        for (int i=0;i<n;i++) {
            int[] s = typeStats.getOrDefault(types[i], new int[]{0,0});
            float gx = pl + i*groupW + groupW*0.08f;
            float eh = (float)s[0]/maxVal*h;
            float dh = (float)s[1]/maxVal*h;

            g2.setColor(UIColors.hex("#4ade80"));
            g2.fillRoundRect((int)gx,(int)(pt+h-eh),(int)barW,(int)Math.max(eh,1),2,2);
            g2.setColor(UIColors.hex("#f87171"));
            g2.fillRoundRect((int)(gx+barW+2),(int)(pt+h-dh),(int)barW,(int)Math.max(dh,1),2,2);

            if(s[0]>0){ g2.setColor(UIColors.hex("#4ade80")); g2.setFont(new Font("SansSerif",Font.PLAIN,9)); g2.drawString(String.valueOf(s[0]),(int)gx,(int)(pt+h-eh-2)); }
            if(s[1]>0){ g2.setColor(UIColors.hex("#f87171")); g2.setFont(new Font("SansSerif",Font.PLAIN,9)); g2.drawString(String.valueOf(s[1]),(int)(gx+barW+2),(int)(pt+h-dh-2)); }

            g2.setColor(UIColors.hex("#475569")); g2.setFont(new Font("SansSerif",Font.PLAIN,9));
            g2.drawString(labels[i],(int)(gx+barW/2-8),pt+h+pb-5);
        }

        int lx = pl+w-130, ly = pt+8;
        g2.setColor(UIColors.hex("#4ade80")); g2.fillRoundRect(lx,ly-7,8,8,2,2);
        g2.setColor(UIColors.hex("#64748b")); g2.setFont(new Font("SansSerif",Font.PLAIN,9)); g2.drawString("Ewakuowani",lx+11,ly);
        g2.setColor(UIColors.hex("#f87171")); g2.fillRoundRect(lx+68,ly-7,8,8,2,2);
        g2.setColor(UIColors.hex("#64748b")); g2.drawString("Zabici",lx+79,ly);
    }
}