package po.simulation.agent;

import po.simulation.board.Board;
import po.simulation.model.AgentState;

public class Injured extends Agent {
    private boolean internalTickFlip = false;

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


       //mobes only 2nd tick
        internalTickFlip = !internalTickFlip;
        if (!internalTickFlip) return;
        waitForHelp();
    }
    @Override
    public char getDisplayChar() {
        return 'I';
    }
    public void waitForHelp() {
        // stays in place or moves slowly to exit
        checkEvacuated();
    }

    public boolean needsHelp() {
        return state == AgentState.INJURED;
    }
}