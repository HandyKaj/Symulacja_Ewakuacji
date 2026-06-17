package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;
import java.util.Collections;
import java.util.List;

/**
 * Panikujący agent — porusza się chaotycznie i zaraża paniką sąsiadów.
 * Ignoruje oznakowanie wyjść. Wysoki początkowy poziom paniki (85).
 * Może zarazić spokojnego agenta zwiększając jego panikę o 25.
 */
public class Panicking extends Agent {

    /**
     * Tworzy panikującego agenta z wysokim poziomem paniki początkowej (85).
     *
     * @param id    unikalny identyfikator agenta
     * @param name  nazwa agenta
     * @param board plansza symulacji
     * @param x     początkowa współrzędna pozioma
     * @param y     początkowa współrzędna pionowa
     */
    public Panicking(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 1.0f;
        this.panic = 85;// High initial panic
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

    /** Losowy ruch — tasuje sąsiednie komórki i idzie na pierwszą wolną.
     *  * @param neighbors lista sąsiednich komórek do sprawdzenia */
    private void chaosMove(List<Cell> neighbors) {
        Collections.shuffle(neighbors);// Randomize directions
        for (Cell neighbor : neighbors) {
            if (neighbor.isPassable() && neighbor.isEmpty()) {
                moveTo(neighbor.getX(), neighbor.getY());
                break;
            }
        }
    }

    /** Zaraża paniką sąsiednich spokojnych agentów — zwiększa ich panikę o 25.
     *
        * @param neighbors lista sąsiednich komórek do sprawdzenia */
    private void spreadPanic(List<Cell> neighbors) {
        for (Cell neighbor : neighbors) {
            if (!neighbor.isEmpty()) {
                Agent other = neighbor.getAgent();
                if (other instanceof Calm) {
                    other.setPanic(other.getPanic() + 25);
                }
            }
        }
    }
}