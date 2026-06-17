package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;
import java.util.List;

/**
 * Agent altruista — idzie do wyjścia najkrótszą drogą, ale aktywnie szuka rannych.
 * Gdy znajdzie rannego w zasięgu wzroku (prostokąt 10×5), podchodzi do niego
 * i prowadzi go do wyjścia z prędkością 0.5 (co drugi tick).
 */
public class Altruist extends Agent {

    /** Ranny agent aktualnie prowadzony przez altruistę, lub null jeśli brak. */
    private Injured rescuedAgent = null;

    /** Przełącznik do symulacji prędkości 0.5 podczas prowadzenia rannego — ruch co drugi tick. */
    private boolean internalTickFlip = false;

    /**
     * Tworzy altruistę na podanej pozycji.
     *
     * @param id    unikalny identyfikator agenta
     * @param name  nazwa agenta
     * @param board plansza symulacji
     * @param x     początkowa współrzędna pozioma
     * @param y     początkowa współrzędna pionowa
     */
    public Altruist(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 1.0f;
    }

    @Override
    public void step() {
        if (state == AgentState.EVACUATED || state == AgentState.DEAD) return;
        if (rescuedAgent != null) { // carrying injured person - speed =0,5
            internalTickFlip = !internalTickFlip;
            if (!internalTickFlip) return;

            moveTowardsExitWithInjured();
            return;
        }
        // Scan the custom rectangular vision field (10x in length, 5y in width)
        Injured target = scanForInjuredPeople();
        if (target != null) {
            rescueTarget(target);
        } else {
            // base calm pathfinding
            moveNormallyToExit();
        }
    }

    /**
     * Skanuje prostokątne pole widzenia (10 pól w przód, ±2 w bok)
     * w poszukiwaniu rannego agenta wymagającego pomocy.
     *
     * @return znaleziony ranny agent lub null jeśli brak
     */
    private Injured scanForInjuredPeople() {
        // Vision in shape of a rectangle forward 10, width -2 to 2
        for (int dx = 0; dx <= 10; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int targetX = this.x + dx;
                int targetY = this.y + dy;

                if (board.inBounds(targetX, targetY)) {
                    Cell cell = board.getCell(targetX, targetY);
                    if (cell != null && !cell.isEmpty() && cell.getAgent() instanceof Injured) {
                        Injured injured = (Injured) cell.getAgent();
                        if (injured.needsHelp()) {
                            return injured;
                        }
                    }
                }
            }
        }
        return null;
    }

    /** Podchodzi do rannego i przejmuje go (ustawia stan CARRIED).
     *
          * @param target ranny agent do uratowania */
    private void rescueTarget(Injured target) {

        int nextX = this.x + Integer.compare(target.getX(), this.x);
        int nextY = this.y + Integer.compare(target.getY(), this.y);

        if (board.inBounds(nextX, nextY) && board.getCell(nextX, nextY).isPassable()) {
            // when next to injured
            if (Math.abs(this.x - target.getX()) <= 1 && Math.abs(this.y - target.getY()) <= 1) {
                this.rescuedAgent = target;
                target.setState(AgentState.CARRIED);
            } else {
                moveTo(nextX, nextY);
            }
        }
    }

    /**
     * Prowadzi rannego do wyjścia — altruista przesuwa się na najlepszą komórkę,
     * a ranny zajmuje jego poprzednią pozycję. Po dotarciu do wyjścia obaj są ewakuowani.
     */
    private void moveTowardsExitWithInjured() {
        List<Cell> neighbors = perceive();
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
            // Move both Altruist and Injured agent together
            int oldX = this.x;
            int oldY = this.y;
            moveTo(bestCell.getX(), bestCell.getY());
            rescuedAgent.moveTo(oldX, oldY); // Move the escorted injured agent to the old position of Altruist
        }

        // Check if exit is reached to evacuate both agents
        Cell currentCell = board.getCell(x, y);
        if (currentCell != null && currentCell.getType() == po.simulation.model.CellType.EXIT) {
            this.state = AgentState.EVACUATED;
            rescuedAgent.setState(AgentState.EVACUATED);
            board.removeAgent(this);
            board.removeAgent(rescuedAgent);

        }
    }

    /** Idzie do wyjścia najkrótszą drogą BFS gdy nie ma rannych w pobliżu. */
    private void moveNormallyToExit() {
        List<Cell> neighbors = perceive();
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
        if (bestCell != null) moveTo(bestCell.getX(), bestCell.getY());
        checkEvacuated();
    }
}