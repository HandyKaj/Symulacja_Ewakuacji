package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.fire.Fire;
import po.simulation.model.AgentState;
import java.util.List;

public class Firefighter extends Agent {
    private int firefightingRange = 2;
    private int effectiveness = 25;

    public Firefighter(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 1.0f;
    }

    @Override
    public void step() {
        // turn off fire
        suppressFireInVicinity();
        moveTowardsExit(); // lead toward closest exit
    }

    @Override
    public char getDisplayChar() {
        return 'S';
    }

    private void suppressFireInVicinity() {
        for (int dx = -firefightingRange; dx <= firefightingRange; dx++) {
            for (int dy = -firefightingRange; dy <= firefightingRange; dy++) {
                int targetX = this.x + dx;
                int targetY = this.y + dy;

                if (board.inBounds(targetX, targetY)) {
                    Cell cell = board.getCell(targetX, targetY);
                    if (cell != null && cell.hasFire()) {
                        Fire fire = cell.getFire();
                        fire.reduce(effectiveness);

                        // if all fire is gone then remove it completly
                        if (fire.getIntensity() <= 0) {
                            cell.setFire(null);
                        }
                    }
                }
            }
        }
    }

    private void moveTowardsExit() {
        List<Cell> neighbors = perceive();
        Cell bestCell = null;
        int minDistance = Integer.MAX_VALUE;
        for (Cell neighbor : neighbors) {
            // Firefighters gain higher fire tolerence
            if (neighbor.getType() != po.simulation.model.CellType.WALL && neighbor.isEmpty()) {
                int dist = board.distanceToExit(neighbor.getX(), neighbor.getY());
                if (dist != -1 && dist < minDistance) {
                    minDistance = dist;
                    bestCell = neighbor;
                }
            }
        }

        if (bestCell != null) {
            moveTo(bestCell.getX(), bestCell.getY());
        }
    }
}