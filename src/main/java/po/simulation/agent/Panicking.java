package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;
import java.util.Collections;
import java.util.List;

public class Panicking extends Agent {

    public Panicking(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 1.0f;
        this.panic = 85; // High initial panic
    }

    @Override
    public void step() {
        if (state == AgentState.EVACUATED || state == AgentState.DEAD){
            return;
        }

        List<Cell> neighbors = perceive();
        updatePanic(neighbors);
        spreadPanic(neighbors);
        if (this.panic < 30) {
            // Calmed down behavior can be triggered
        }

        chaosMove(neighbors);
        checkEvacuated();
    }
    @Override
    public char getDisplayChar() {
        return 'P';
    }

    private void chaosMove(List<Cell> neighbors) {
        Collections.shuffle(neighbors); // Randomize directions
        for (Cell neighbor : neighbors) {
            if (neighbor.isPassable() && neighbor.isEmpty()) {
                moveTo(neighbor.getX(), neighbor.getY());
                break;
            }
        }
    }

    private void spreadPanic(List<Cell> neighbors) {
        for (Cell neighbor : neighbors) {
            if (!neighbor.isEmpty()) {
                Agent other = neighbor.getAgent();
                if (other instanceof Calm) {
                    // Increase neighbor panic probability
                    other.setPanic(other.getPanic() + 25);
                }
            }
        }
    }
}