package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;
import java.util.List;

public class Calm extends Agent {

    public Calm(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 1.0f;
    }

    @Override
    public void step() {
        if (state == AgentState.EVACUATED || state == AgentState.DEAD) return;

        List<Cell> neighbors = perceive();
        updatePanic(neighbors);

        // Checking for panic requirement
        if (this.panic >= 70) {
            chaosMove();
            return;
        }

        Cell followCell = findLeaderToFollow(neighbors);
        if (followCell != null) {
            moveTo(followCell.getX(), followCell.getY());
            checkEvacuated();
            return;
        }

        //Optimal path to exit by length
        Cell bestCell = null;
        int minDistance = Integer.MAX_VALUE;
        for (Cell neighbor : neighbors) {
            if (neighbor.isPassable() && neighbor.isEmpty()) {
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
        checkEvacuated();
    }
    @Override
    public char getDisplayChar() {
        return 'C';
    }

    private Cell findLeaderToFollow(List<Cell> neighbors) {
        for (Cell neighbor : neighbors) {
            if (!neighbor.isEmpty()) {
                Agent leadingAgent = neighbor.getAgent();
                if (leadingAgent instanceof Firefighter || leadingAgent instanceof Altruist) {
                    // Follow 1 step behind if possible
                    return neighbor;
                }
            }
        }
        return null;
    }

    private void chaosMove() {
        List<Cell> neighbors = perceive();
        for (Cell n : neighbors) {
            if (n.isPassable() && n.isEmpty()) {
                moveTo(n.getX(), n.getY());
                break;
            }
        }
    }
}