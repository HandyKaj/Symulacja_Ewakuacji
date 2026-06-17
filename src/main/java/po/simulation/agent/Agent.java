package po.simulation.agent;


import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.model.AgentState;

import java.util.List;
/**
 * Abstrakcyjna klasa bazowa dla wszystkich agentów w symulacji ewakuacji.
 * Każdy agent ma pozycję na planszy, poziom paniki i stan.
 * Konkretne zachowanie jest implementowane przez podklasy.
 */
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

    /**
     * Tworzy nowego agenta i umieszcza go na podanej pozycji.
     * Domyślny stan to IN_BUILDING, prędkość 1.0, panika 0.
     *
     * @param id    unikalny identyfikator agenta
     * @param name  nazwa agenta
     * @param board plansza na której przebywa agent
     * @param x     początkowa współrzędna pozioma
     * @param y     początkowa współrzędna pionowa
     */
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

    /** Wykonuje jeden krok symulacji — implementacja zależy od typu agenta. */
    public abstract void step();

/** Zwraca listę sąsiednich komórek (góra, dół, lewo, prawo). */
    public List<Cell> perceive() {
        return board.getNeighbors(x, y);
    }

    /**
     * Przesuwa agenta na podaną pozycję jeśli komórka jest przejezdna.
     * Agent może wejść na wyjście nawet jeśli jest zajęte przez innego agenta.
     * @param newX nowa współrzędna pozioma
     * @param newY nowa współrzędna pionowa
     */
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

    /**
     * Aktualizuje poziom paniki — rośnie o 15 za każdą sąsiednią komórkę z ogniem,
     * maleje o 2 co tick. Wartość jest ograniczona do zakresu 0–100.
     *  @param neighbors lista sąsiednich komórek do analizy
     */
    public void updatePanic(List<Cell> neighbors) {
        int fireCells = 0;
        for (Cell c : neighbors) {
            if (c.hasFire()) fireCells++;
        }
        setPanic(panic + fireCells * 15 - 2);
    }

    /** Ewakuuje agenta jeśli stoi na polu wyjścia — usuwa go z planszy. */
    public void checkEvacuated() {
        Cell current = board.getCell(x, y);
        if (current != null && current.getType() == po.simulation.model.CellType.EXIT) {
            state = AgentState.EVACUATED;
            board.removeAgent(this);
        }
    }
    /** @return true jeśli agent nie jest martwy */
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