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
import java.util.*;

/**
 * Główna klasa symulacji ewakuacji budynku podczas pożaru.
 * Zarządza cyklem ticków: ruch agentów, interakcje z ogniem,
 * rozprzestrzenianie ognia i aktualizacja metryk.
 * Symulacja kończy się gdy wszyscy agenci opuszczą budynek.
 */
public class Simulation {
    private Board board;
    private List<Agent> agents;
    private int stepCount;
    private boolean isRunning;
    private SimConfig config;
    private SimMetrics metrics;
    private final Map<String, Integer> evacuatedByType = new HashMap<>();
    private final Map<String, Integer> deadByType = new HashMap<>();

    /**
     * Tworzy nową symulację o podanych wymiarach planszy i konfiguracji.
     *
     * @param width  szerokość planszy
     * @param height wysokość planszy
     * @param config parametry konfiguracyjne symulacji
     */
    public Simulation(int width, int height, SimConfig config) {
        this.board = new Board(width, height);
        this.agents = new ArrayList<>();
        this.stepCount = 0;
        this.isRunning = false;
        this.config = config;
        this.metrics = new SimMetrics();
    }

    /**
     * Inicjalizuje planszę — ustawia ściany zewnętrzne, wyjście awaryjne
     * i startowe ognisko pożaru w środku budynku.
     */
    public void initialize() {
        for (int x = 0; x < board.getWidth(); x++) {
            board.getCell(x, 0).setType(CellType.WALL);
            board.getCell(x, board.getHeight() - 1).setType(CellType.WALL);
        }
        for (int y = 0; y < board.getHeight(); y++) {
            board.getCell(0, y).setType(CellType.WALL);
            board.getCell(board.getWidth() - 1, y).setType(CellType.WALL);
        }

        board.getCell(board.getWidth() - 1, board.getHeight() / 2).setType(CellType.EXIT);
        int fireX = board.getWidth() / 2;
        int fireY = board.getHeight() / 2;
        board.getCell(fireX, fireY).setFire(new Fire(30));
        isRunning = true;
    }


    public Map<String, Integer> getEvacuatedByType() {
        return evacuatedByType;
    }

    public Map<String, Integer> getDeadByType() {
        return deadByType;
    }

    /**
     * Wykonuje jeden krok (tick) symulacji w następującej kolejności:
     * <ol>
     *   <li>Każdy żywy agent wykonuje swój step()</li>
     *   <li>Sprawdzenie interakcji agentów z ogniem</li>
     *   <li>Rozprzestrzenianie ognia</li>
     *   <li>Rejestracja ewakuowanych agentów w metrykach</li>
     *   <li>Usunięcie ewakuowanych i martwych agentów z listy</li>
     *   <li>Sprawdzenie warunku końca symulacji</li>
     * </ol>
     */
    public void step() {
        if (!isRunning) return;

        stepCount++;

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

        for (Agent agent : snapshot) {
            if (agent.getState() == AgentState.EVACUATED) {
                String type = agent.getClass().getSimpleName();
                evacuatedByType.merge(type, 1, Integer::sum);
                metrics.registerEvacuation(stepCount);
            }
        }



        agents.removeIf(a -> a.getState() == AgentState.EVACUATED || a.getState() == AgentState.DEAD);
        if (agents.isEmpty()) {
            isRunning = false;
        }
    }

    /**
     * Dodaje agenta do symulacji i umieszcza go na planszy.
     *
     * @param agent agent do dodania
     */
    public void addAgent(Agent agent) {
        agents.add(agent);
        board.placeAgent(agent, agent.getX(), agent.getY());
    }

    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * @return liczba agentów aktualnie przebywających w budynku
     * (stany: IN_BUILDING, INJURED, CARRIED)
     */
    public long getAliveCount() {
        return agents.stream()
                .filter(a -> a.getState() == po.simulation.model.AgentState.IN_BUILDING
                        || a.getState() == po.simulation.model.AgentState.INJURED
                        || a.getState() == po.simulation.model.AgentState.CARRIED)
                .count();
    }

    /** @return liczba rannych agentów aktualnie w budynku */
    public long getInjuredCount() {
        return agents.stream()
                .filter(a -> a.getState() == po.simulation.model.AgentState.INJURED)
                .count();
    }

    /**
     * Obsługuje interakcję agenta z ogniem na jego komórce.
     * intensity {@literal >} 90 → śmierć; intensity {@literal >} 50 → ranny.
     * Dodatkowo agent zablokowany przez 30 ticków z rzędu (brak dostępnej drogi
     * do wyjścia) ginie z powodu uwięzienia.
     *
     * @param agent agent do sprawdzenia
     */
    private void handleAgentOnFire(Agent agent) {
        Cell cell = board.getCell(agent.getX(), agent.getY());
        if (cell != null && cell.hasFire()) {
            int intensity = cell.getFire().getIntensity();
            if (intensity > 90) {
                agent.setState(AgentState.DEAD);
                board.removeAgent(agent);
                metrics.registerDeath();
                String type = agent.getClass().getSimpleName();
                deadByType.merge(type, 1, Integer::sum);
            } else if (intensity > 50 && agent.getState() == AgentState.IN_BUILDING) {
                agent.setState(AgentState.INJURED);
            }
        }
        boolean canMove = board.getNeighbors(agent.getX(), agent.getY())
                .stream().anyMatch(c -> c.isPassable() && c.isEmpty())
        && board.distanceToExit(agent.getX(), agent.getY()) != -1;

        if (!canMove) {
            agent.setStuckTicks(agent.getStuckTicks() + 1);
            if (agent.getStuckTicks() >= 30) {
                agent.setState(AgentState.DEAD);
                board.removeAgent(agent);
                metrics.registerDeath();
                String type = agent.getClass().getSimpleName();
                deadByType.merge(type, 1, Integer::sum);
            }
        } else {
            agent.setStuckTicks(0);
        }

    }

    public SimMetrics getMetrics() { return metrics; }
    public Board getBoard()        { return board; }
    public int getStepCount()      { return stepCount; }
    public boolean isRunning()     { return isRunning; }

}