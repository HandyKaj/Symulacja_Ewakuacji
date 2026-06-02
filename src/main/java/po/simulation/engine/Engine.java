package po.simulation.engine;

import po.simulation.agent.Agent;
import po.simulation.board.Board;
import java.util.Collections;
import java.util.List;

public class Engine {
    private Board board;

    public Engine(Board board) {
        this.board = board;
    }
    public void runSingleTick() {
        List<Agent> activeAgents = board.getAgents();
        Collections.shuffle(activeAgents); //shuffle so order of moves chagnes

        for (Agent agent : activeAgents) {
            if (agent.isAlive()) {
                agent.step();
            }
        }
        board.spreadFire();
    }
}