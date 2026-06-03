package po.simulation;


import po.simulation.agent.Agent;
import po.simulation.board.Board;
import po.simulation.board.Cell;
import po.simulation.config.SimConfig;
import po.simulation.fire.Fire;
import po.simulation.metrics.SimMetrics;
import po.simulation.model.AgentState;
import po.simulation.model.CellType;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private Board board;
    private List<Agent> agents;
    private int stepCount;
    private boolean isRunning;
    private SimConfig config;
    private SimMetrics metrics;

    public Simulation(int width, int height, SimConfig config) {
        this.board = new Board(width, height);
        this.agents = new ArrayList<>();
        this.stepCount = 0;
        this.isRunning = false;
        this.config = config;
        this.metrics = new SimMetrics();
    }


    public void initialize() {

        // Ściany dookoła
        for (int x = 0; x < board.getWidth(); x++) {
            board.getCell(x, 0).setType(CellType.WALL);
            board.getCell(x, board.getHeight() - 1).setType(CellType.WALL);
        }
        for (int y = 0; y < board.getHeight(); y++) {
            board.getCell(0, y).setType(CellType.WALL);
            board.getCell(board.getWidth() - 1, y).setType(CellType.WALL);
        }

        // Wyjście awaryjne
        board.getCell(board.getWidth() - 1, board.getHeight() / 2).setType(CellType.EXIT);

        // Startowe ognisko pożaru
        int fireX = board.getWidth() / 2;
        int fireY = board.getHeight() / 2;
        board.getCell(fireX, fireY).setFire(
                new Fire(30)
        );

        isRunning = true;
        System.out.println("Symulacja zainicjalizowana. Plansza " +
                board.getWidth() + "x" + board.getHeight());
    }


    public void step() {
        if (!isRunning) return;

        stepCount++;
        System.out.println("\n--- Tick " + stepCount + " ---");

        List<Agent> snapshot = new ArrayList<>(agents);
        for (Agent agent : snapshot) {
            if (agent.isAlive()) {
                agent.step();
            }
        }


        for (Agent agent : snapshot) {
            handleAgentOnFire(agent);
        }
        board.spreadFire();


        if (stepCount == config.getFirefighterDelay()) {
            spawnFirefighters();
        }
        for (Agent agent : snapshot) {
            if (agent.getState() == AgentState.EVACUATED) {
                metrics.registerEvacuation(stepCount);
            }
        }

        agents.removeIf(a -> a.getState() == AgentState.EVACUATED
                || a.getState() == AgentState.DEAD);


        board.printBoard();
        printStatus();


        if (agents.isEmpty()) {
            isRunning = false;
            System.out.println("\nSymulacja zakończona po " + stepCount + " tickach.");
            metrics.export();
        }
    }


    public void run(int steps) {
        isRunning = true;
        for (int i = 0; i < steps && isRunning; i++) {
            step();
        }
    }


    public void addAgent(Agent agent) {
        agents.add(agent);
        board.placeAgent(agent, agent.getX(), agent.getY());
    }


    public void removeAgent(Agent agent) {
        agents.remove(agent);
        board.removeAgent(agent);
    }


    private void handleAgentOnFire(Agent agent) {
        Cell cell = board.getCell(agent.getX(), agent.getY());
        if (cell != null && cell.hasFire()) {
            int intensity = cell.getFire().getIntensity();
            if (intensity > 90) {
                agent.setState(AgentState.DEAD);
                board.removeAgent(agent);
                metrics.registerDeath();
                System.out.println(agent + " zginął w ogniu!");
            } else if (intensity > 50 && agent.getState() == AgentState.IN_BUILDING) {
                agent.setState(AgentState.INJURED);
                System.out.println(agent + " został ranny!");
            }
        }
    }


    private void spawnFirefighters() {

        System.out.println("Strażacy wkraczają do budynku!");
    }


    private void printStatus() {
        long alive = agents.stream()
                .filter(a -> a.getState() == AgentState.IN_BUILDING
                        || a.getState() == AgentState.INJURED
                        || a.getState() == AgentState.CARRIED).count();
        long injured = agents.stream()
                .filter(a -> a.getState() == AgentState.INJURED).count();
        System.out.println("W budynku: " + alive +
                " | Rannych: " + injured +
                " | Ewakuowanych: " + metrics.getEvacuatedCount() +
                " | Zabitych: " + metrics.getDeadCount());
    }

    public SimMetrics getMetrics() { return metrics; }
    public Board getBoard()        { return board; }
    public int getStepCount()      { return stepCount; }
    public boolean isRunning()     { return isRunning; }

    public void exportMetrics() {
        metrics.export();
    }
}