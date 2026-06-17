package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.fire.Fire;
import java.util.List;

/**
 * Strażak — tłumi ogień w zasięgu i prowadzi rannych do wyjścia.
 * Dopóki istnieją dostępni agenci w budynku, strażak porusza się w ich kierunku
 * i gasi ogień. Gdy wszyscy są niedostępni, ewakuuje się sam.
 */
public class Firefighter extends Agent {

    /** Zasięg gaszenia ognia (w polach od strażaka). */
    private int firefightingRange = 2;

    /** Skuteczność gaszenia — o ile jednostek zmniejsza intensywność ognia co tick. */
    private int effectiveness = 25;

    /**
     * Tworzy strażaka na podanej pozycji.
     *
     * @param id    unikalny identyfikator agenta
     * @param name  nazwa agenta
     * @param board plansza symulacji
     * @param x     początkowa współrzędna pozioma
     * @param y     początkowa współrzędna pionowa
     */
    public Firefighter(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 1.0f;
    }

    @Override
    public void step() {
            suppressFireInVicinity();

            boolean canReachSomeone = board.getAgents().stream()
                    .filter(a -> a != this && !(a instanceof Firefighter))
                    .anyMatch(a -> board.distanceToExit(a.getX(), a.getY()) != -1);

            if (canReachSomeone) {
                guideInjured();
                moveTowardsAgents();
            } else {
                moveTowardsExit();
            }
        checkEvacuated();
        }

    /** Redukuje intensywność ognia we wszystkich komórkach w zasięgu firefightingRange. */
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

    /** Idzie do wyjścia BFS — strażak ignoruje ogień (wyższa tolerancja). */
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

    /**
     * Przesuwa sąsiedniego rannego agenta o jeden krok w kierunku wyjścia.
     * Strażak nie zabiera rannego jak altruista — tylko go ukierunkowuje.
     */
    public void guideInjured() {
        List<Cell> neighbors = perceive();
        for (Cell c : neighbors) {
            if (c.getAgent() instanceof Injured injured && injured.needsHelp()) {
                int dist = board.distanceToExit(c.getX(), c.getY());
                List<Cell> injuredNeighbors = board.getNeighbors(c.getX(), c.getY());
                Cell best = null;
                int minDist = dist;
                for (Cell n : injuredNeighbors) {
                    if (n.isPassable() && n.isEmpty()) {
                        int d = board.distanceToExit(n.getX(), n.getY());
                        if (d != -1 && d < minDist) {
                            minDist = d;
                            best = n;
                        }
                    }
                }
                if (best != null) {
                    injured.moveTo(best.getX(), best.getY());
                    injured.checkEvacuated();
                }
                break;
            }
        }
    }

    /** Idzie w kierunku agentów w budynku używając BFS do wyjścia jako heurystyki. */
    private void moveTowardsAgents() {
        List<Cell> neighbors = perceive();
        Cell bestCell = null;
        int minDistance = Integer.MAX_VALUE;

        for (Cell neighbor : neighbors) {
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