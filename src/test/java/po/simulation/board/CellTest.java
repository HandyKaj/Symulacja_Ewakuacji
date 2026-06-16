package po.simulation.board;

import org.junit.jupiter.api.Test;
import po.simulation.fire.Fire;
import po.simulation.model.CellType;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void isEmptyWhenNoAgent() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        assertTrue(cell.isEmpty());
    }

    @Test
    void isNotEmptyWhenAgentSet() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        cell.setAgent(null);
        assertTrue(cell.isEmpty()); // null = brak agenta
    }

    @Test
    void hasFireWhenFireSet() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        cell.setFire(new Fire(50));
        assertTrue(cell.hasFire());
    }

    @Test
    void noFireByDefault() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        assertFalse(cell.hasFire());
    }

    @Test
    void wallIsNotPassable() {
        Cell cell = new Cell(0, 0, CellType.WALL);
        assertFalse(cell.isPassable());
    }

    @Test
    void corridorIsPassable() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        assertTrue(cell.isPassable());
    }

    @Test
    void exitIsPassable() {
        Cell cell = new Cell(0, 0, CellType.EXIT);
        assertTrue(cell.isPassable());
    }

    @Test
    void cellWithBlockingFireIsNotPassable() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        Fire fire = new Fire(80); // intensity 80 > threshold 50
        cell.setFire(fire);
        assertFalse(cell.isPassable());
    }

    @Test
    void cellWithLowFireIsPassable() {
        Cell cell = new Cell(0, 0, CellType.CORRIDOR);
        Fire fire = new Fire(20); // intensity 20 < threshold 50
        cell.setFire(fire);
        assertTrue(cell.isPassable());
    }

    @Test
    void getTypeReturnsCorrectType() {
        Cell cell = new Cell(3, 5, CellType.ROOM);
        assertEquals(CellType.ROOM, cell.getType());
    }

    @Test
    void getCoordsReturnCorrectValues() {
        Cell cell = new Cell(3, 5, CellType.CORRIDOR);
        assertEquals(3, cell.getX());
        assertEquals(5, cell.getY());
    }
}