package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.model.AgentState;

/**
 * Ranny agent — porusza się z prędkością 0.5 (co drugi tick).
 * Od startu ma stan INJURED i wymaga pomocy altruisty lub strażaka
 * do skutecznej ewakuacji.
 */
public class Injured extends Agent {

    /** Przełącznik do symulacji prędkości 0.5 — ruch tylko co drugi tick. */
    private boolean internalTickFlip = false;

    /**
     * Tworzy rannego agenta z prędkością 0.5 i stanem INJURED.
     *
     * @param id    unikalny identyfikator agenta
     * @param name  nazwa agenta
     * @param board plansza symulacji
     * @param x     początkowa współrzędna pozioma
     * @param y     początkowa współrzędna pionowa
     */
    public Injured(int id, String name, Board board, int x, int y) {
        super(id, name, board, x, y);
        this.speed = 0.5f;
        this.state = AgentState.INJURED;
    }

    @Override
    public void step() {
        if (state == AgentState.EVACUATED || state == AgentState.DEAD || state == AgentState.CARRIED) {
            return;
        }
        internalTickFlip = !internalTickFlip;//mobes only 2nd tick
        if (!internalTickFlip) return;
        waitForHelp();
    }

    /** Oczekuje na pomoc altruisty lub strażaka. Sprawdza czy dotarł do wyjścia. */
    public void waitForHelp() {
        checkEvacuated();
    }

    /** @return true jeśli agent wymaga pomocy (ma stan INJURED) */
    public boolean needsHelp() {
        return state == AgentState.INJURED;
    }
}