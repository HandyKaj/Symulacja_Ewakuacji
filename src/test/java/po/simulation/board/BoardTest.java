package po.simulation.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import po.simulation.model.CellType;
import static org.junit.jupiter.api.Assertions.*;


class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10, 10);
    }

    @Test
    void inBoundsReturnsTrueForValidCoords() {
        assertTrue(board.inBounds(0, 0));
        assertTrue(board.inBounds(9, 9));
        assertTrue(board.inBounds(5, 5));
    }

    @Test
    void inBoundsReturnsFalseForNegativeCoords() {
        assertFalse(board.inBounds(-1, 0));
        assertFalse(board.inBounds(0, -1));
    }

    @Test
    void inBoundsReturnsFalseForOutOfBoundsCoords() {
        assertFalse(board.inBounds(10, 0));
        assertFalse(board.inBounds(0, 10));
    }

    @Test
    void getCellReturnsNullForOutOfBounds() {
        assertNull(board.getCell(-1, 0));
        assertNull(board.getCell(10, 10));
    }

    @Test
    void getCellReturnsCorrectCell() {
        Cell cell = board.getCell(3, 4);
        assertNotNull(cell);
        assertEquals(3, cell.getX());
        assertEquals(4, cell.getY());
    }

    @Test
    void isEmptyReturnsTrueByDefault() {
        assertTrue(board.isEmpty(5, 5));
    }

    @Test
    void isEmptyReturnsFalseForOutOfBounds() {
        assertFalse(board.isEmpty(-1, 0));
    }

    @Test
    void getNeighboursReturns4ForMiddleCell() {
        assertEquals(4, board.getNeighbors(5, 5).size());
    }

    @Test
    void getNeighboursReturns2ForCornerCell() {
        assertEquals(2, board.getNeighbors(0, 0).size());
    }

    @Test
    void getNeighboursReturns3ForEdgeCell() {
        assertEquals(3, board.getNeighbors(0, 5).size());
    }

    @Test
    void distanceToExitReturnsMinusOneWhenNoExit() {
        assertEquals(-1, board.distanceToExit(0, 0));
    }

    @Test
    void distanceToExitReturnsZeroWhenOnExit() {
        board.getCell(5, 5).setType(CellType.EXIT);
        assertEquals(0, board.distanceToExit(5, 5));
    }

    @Test
    void distanceToExitReturnsCorrectDistance() {
        board.getCell(9, 5).setType(CellType.EXIT);
        int dist = board.distanceToExit(5, 5);
        assertEquals(4, dist);
    }

    @Test
    void distanceToExitReturnsMinusOneWhenBlocked() {
        board.getCell(9, 5).setType(CellType.EXIT);
        // zablokuj wszystkie sąsiednie klasy ścianami
        board.getCell(4, 5).setType(CellType.WALL);
        board.getCell(5, 4).setType(CellType.WALL);
        board.getCell(5, 6).setType(CellType.WALL);
        board.getCell(6, 5).setType(CellType.WALL);
        assertEquals(-1, board.distanceToExit(5, 5));
    }

    @Test
    void getWidthAndHeightCorrect() {
        assertEquals(10, board.getWidth());
        assertEquals(10, board.getHeight());
    }
}