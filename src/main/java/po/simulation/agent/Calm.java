package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;
import java.util.List;

/**
 * Spokojny agent — wybiera najkrótszą drogę do wyjścia algorytmem BFS.
 * Omija ogniska pożaru i podąża za strażakiem lub altruistą jeśli są w pobliżu.
 * Gdy poziom paniki osiągnie 70, zaczyna poruszać się chaotycznie.
 */
public class Calm extends Agent {

    /**
     * Tworzy spokojnego agenta na podanej pozycji.
     *
     * @param id    unikalny identyfikator agenta
     * @param name  nazwa agenta
     * @param board plansza symulacji
     * @param x     początkowa współrzędna pozioma
     * @param y     początkowa współrzędna pionowa
     */
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

    /**
     * Sprawdza czy w sąsiedztwie jest strażak lub altruista.
     * Jeśli tak — agent podąża za nim zamiast szukać własnej drogi.
     *
     * @return komórka z liderem lub null jeśli brak
     */
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

    /** Ruch losowy gdy agent panikuje — pierwsza dostępna wolna komórka. */
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