package po.simulation.ui;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.CellType;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final Board board;
    private final int cs = 6;

    public BoardPanel(Board b) {
        this.board = b;
        setBackground(UIColors.BG);
        setPreferredSize(new Dimension(b.getWidth()*(cs+1), b.getHeight()*(cs+1)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.BOLD, 5));

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell cell = board.getCell(x, y);
                if (cell == null) continue;
                int px = x*(cs+1), py = y*(cs+1);
                Color color;
                if      (cell.getType() == CellType.WALL) color = UIColors.hex("#374151");
                else if (cell.getType() == CellType.EXIT) color = UIColors.hex("#2e7d32");
                else if (cell.hasFire()) {
                    int i = cell.getFire().getIntensity();
                    color = i > 70 ? UIColors.hex("#d32f2f") : UIColors.hex("#ff9800");
                } else if (cell.getAgent() != null) {
                    color = UIColors.agentColor(cell.getAgent().getClass().getSimpleName());
                } else color = UIColors.hex("#1f2937");

                g2.setColor(color);
                g2.fillRoundRect(px, py, cs, cs, 1, 1);

                if (cell.getAgent() != null) {
                    g2.setColor(new Color(255, 255, 255, 200));
                    String letter = cell.getAgent().getClass().getSimpleName().substring(0, 1);
                    g2.drawString(letter, px+1, py+cs-1);
                }
            }
        }
    }
}