
package po.simulation.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import po.simulation.board.Board;
import po.simulation.fire.Fire;
import po.simulation.model.AgentState;
import po.simulation.model.CellType;

import static org.junit.jupiter.api.Assertions.*;

class AgentTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10, 10);
    }

    // ── Calm ──────────────────────────────────────────────────────────────────

    @Test
    void calmHasCorrectInitialState() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        assertEquals(AgentState.IN_BUILDING, calm.getState());
        assertEquals(1.0f, calm.getSpeed());
        assertEquals(0, calm.getPanic());
    }

    @Test
    void calmEvacuatesWhenOnExit() {
        board.getCell(5, 5).setType(CellType.EXIT);
        Calm calm = new Calm(1, "Calm", board, 5, 5);
        board.placeAgent(calm, 5, 5);
        calm.checkEvacuated();
        assertEquals(AgentState.EVACUATED, calm.getState());
    }

    @Test
    void calmDoesNotEvacuateOnCorridor() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        board.placeAgent(calm, 3, 3);
        calm.checkEvacuated();
        assertEquals(AgentState.IN_BUILDING, calm.getState());
    }

    @Test
    void calmPanicIncreasesNearFire() {
        board.getCell(4, 3).setFire(new Fire(80));
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        board.placeAgent(calm, 3, 3);
        int panicBefore = calm.getPanic();
        calm.updatePanic(board.getNeighbors(3, 3));
        assertTrue(calm.getPanic() > panicBefore);
    }

    // ── Panicking ─────────────────────────────────────────────────────────────

    @Test
    void panickingHasHighInitialPanic() {
        Panicking p = new Panicking(1, "Panicking", board, 3, 3);
        assertTrue(p.getPanic() >= 70);
    }

    @Test
    void panickingHasCorrectInitialState() {
        Panicking p = new Panicking(1, "Panicking", board, 3, 3);
        assertEquals(AgentState.IN_BUILDING, p.getState());
    }

    // ── Injured ───────────────────────────────────────────────────────────────

    @Test
    void injuredHasCorrectSpeed() {
        Injured injured = new Injured(1, "Injured", board, 3, 3);
        assertEquals(0.5f, injured.getSpeed());
    }

    @Test
    void injuredHasCorrectInitialState() {
        Injured injured = new Injured(1, "Injured", board, 3, 3);
        assertEquals(AgentState.INJURED, injured.getState());
    }

    @Test
    void injuredNeedsHelp() {
        Injured injured = new Injured(1, "Injured", board, 3, 3);
        assertTrue(injured.needsHelp());
    }

    @Test
    void injuredDoesNotNeedHelpWhenEvacuated() {
        Injured injured = new Injured(1, "Injured", board, 3, 3);
        injured.setState(AgentState.EVACUATED);
        assertFalse(injured.needsHelp());
    }

    // ── Altruist ──────────────────────────────────────────────────────────────

    @Test
    void altruistHasCorrectInitialState() {
        Altruist alt = new Altruist(1, "Altruist", board, 3, 3);
        assertEquals(AgentState.IN_BUILDING, alt.getState());
        assertEquals(1.0f, alt.getSpeed());
    }

    // ── Firefighter ───────────────────────────────────────────────────────────

    @Test
    void firefighterHasCorrectInitialState() {
        Firefighter ff = new Firefighter(1, "Firefighter", board, 3, 3);
        assertEquals(AgentState.IN_BUILDING, ff.getState());
    }

    // ── updatePanic ───────────────────────────────────────────────────────────

    @Test
    void panicDoesNotExceed100() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        calm.setPanic(95);
        board.getCell(4, 3).setFire(new Fire(100));
        board.getCell(3, 4).setFire(new Fire(100));
        board.getCell(2, 3).setFire(new Fire(100));
        board.getCell(3, 2).setFire(new Fire(100));
        calm.updatePanic(board.getNeighbors(3, 3));
        assertTrue(calm.getPanic() <= 100);
    }

    @Test
    void panicDoesNotGoBelowZero() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        calm.setPanic(1);
        calm.updatePanic(board.getNeighbors(3, 3)); // bez ognia — panika spada
        assertTrue(calm.getPanic() >= 0);
    }

    // ── moveTo ────────────────────────────────────────────────────────────────

    @Test
    void agentMovesToPassableCell() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        board.placeAgent(calm, 3, 3);
        calm.moveTo(4, 3);
        assertEquals(4, calm.getX());
        assertEquals(3, calm.getY());
    }

    @Test
    void agentDoesNotMoveToWall() {
        board.getCell(4, 3).setType(CellType.WALL);
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        board.placeAgent(calm, 3, 3);
        calm.moveTo(4, 3);
        assertEquals(3, calm.getX());
        assertEquals(3, calm.getY());
    }

    @Test
    void isAliveReturnsTrueForLivingAgent() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        assertTrue(calm.isAlive());
    }

    @Test
    void isAliveReturnsFalseForDeadAgent() {
        Calm calm = new Calm(1, "Calm", board, 3, 3);
        calm.setState(AgentState.DEAD);
        assertFalse(calm.isAlive());
    }
}