package po.simulation.agent;


import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;

import java.util.List;

public abstract class Agent {

    protected int id;
    protected String name;
    protected int x;
    protected int y;
    protected float speed;
    protected int panic;        // 0–100
    protected AgentState state;
    protected Board board;
    protected int stuckTicks = 0;

    public Agent(int id, String name, Board board, int x, int y) {
        this.id = id;
        this.name = name;
        this.board = board;
        this.x = x;
        this.y = y;
        this.speed = 1.0f;
        this.panic = 0;
        this.state = AgentState.IN_BUILDING;
    }


    public abstract void step();

    public abstract char getDisplayChar(); //name displayed insted of A for everyone

    public List<Cell> perceive() {
        return board.getNeighbors(x, y);
    }


    public void moveTo(int newX, int newY) {
        if (board.inBounds(newX, newY)) {
            Cell target = board.getCell(newX, newY);
            if (target.isPassable() && (target.isEmpty() || target.getType() == po.simulation.model.CellType.EXIT)) {
                board.moveAgent(this, newX, newY);
                this.x = newX;
                this.y = newY;
            }
        }
    }


    public void updatePanic(List<Cell> neighbors) {
        int fireCells = 0;
        for (Cell c : neighbors) {
            if (c.hasFire()) fireCells++;
        }
        setPanic(panic + fireCells * 15 - 2);
    }


    public void checkEvacuated() {
        Cell current = board.getCell(x, y);
        if (current != null && current.getType() == po.simulation.model.CellType.EXIT) {
            state = AgentState.EVACUATED;
            board.removeAgent(this);
        }
    }

    public boolean isAlive() {
        return state != AgentState.DEAD;
    }


    public String getName()      { return name; }
    public int getId()           { return id; }
    public int getX()            { return x; }
    public int getY()            { return y; }
    public float getSpeed()      { return speed; }
    public int getPanic()        { return panic; }
    public AgentState getState() { return state; }
    public Board getBoard()      { return board; }


    public void setX(int x)              { this.x = x; }
    public void setY(int y)              { this.y = y; }
    public void setSpeed(float speed)    { this.speed = speed; }
    public void setState(AgentState s)   { this.state = s; }
    public void setPanic(int panic)      { this.panic = Math.max(0, Math.min(100, panic)); }
    public int getStuckTicks()              { return stuckTicks; }
    public void setStuckTicks(int stuckTicks) { this.stuckTicks = stuckTicks; }

    @Override
    public String toString() {
        return name + " (" + getClass().getSimpleName() + "#" + id + ") " +
                "at (" + x + "," + y + ") panic=" + panic + " state=" + state;
    }
}