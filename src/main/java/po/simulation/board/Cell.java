package po.simulation.board;

import po.simulation.agent.Agent;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;

public class Cell {

    private int x;
    private int y;
    private CellType type;
    private Agent agent;
    private Fire fire;

    public Cell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.agent = null;
        this.fire = null;
    }


    public boolean isEmpty() {
        return agent == null;
    }


    public boolean hasFire() {
        return fire != null;
    }


    public boolean isPassable() {
        if (type == CellType.WALL) return false;
        if (fire != null && fire.isBlocking()) return false;
        return true;
    }


    public int getX()         { return x; }
    public int getY()         { return y; }
    public CellType getType() { return type; }
    public Agent getAgent()  { return agent; }
    public Fire getFire()     { return fire; }



    public void setAgent(Agent agent) { this.agent = agent; }
    public void setFire(Fire fire)     { this.fire = fire; }
    public void setType(CellType type) { this.type = type; }

    @Override
    public String toString() {
        return "Cell(" + x + "," + y + ") type=" + type +
                (hasFire() ? " FIRE=" + fire.getIntensity() : "") +
                (isEmpty() ? "" : " [agent]");
    }
}