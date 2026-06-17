package po.simulation.board;

import po.simulation.agent.Agent;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;

/**
 * Pojedyncza komórka na planszy symulacji.
 * Może zawierać agenta i ogień. Typ komórki określa czy jest przejezdna.
 */
public class Cell {
    private int x;
    private int y;
    private CellType type;
    private Agent agent;
    private Fire fire;

    /**
     * Tworzy nową komórkę o podanych współrzędnych i typie.
     * Komórka jest początkowo pusta — bez agenta i bez ognia.
     *
     * @param x    współrzędna pozioma
     * @param y    współrzędna pionowa
     * @param type typ komórki (korytarz, ściana, wyjście itp.)
     */
    public Cell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.agent = null;
        this.fire = null;
    }

    /** @return true jeśli na komórce nie ma agenta */
    public boolean isEmpty() { return agent == null; }

    /** @return true jeśli na komórce nie ma agenta */
    public boolean hasFire() {
        return fire != null;
    }

    /**
     * @return true jeśli agent może wejść na tę komórkę —
     * false dla ścian i komórek z blokującym ogniem
     */
    public boolean isPassable() {
        if (type == CellType.WALL) return false;
        if (fire != null && fire.isBlocking()) return false;
        return true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public CellType getType() { return type; }
    public Agent getAgent()  { return agent; }
    public Fire getFire()     { return fire; }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setFire(Fire fire) {
        this.fire = fire;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Cell(" + x + "," + y + ") type=" + type + (hasFire() ? " FIRE=" + fire.getIntensity() : "") + (isEmpty() ? "" : " [agent]");
    }
}