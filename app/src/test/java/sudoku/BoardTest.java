package sudoku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Arrays;


public class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(); 
    }

    @Test
    void testSetCellValid() {
        assertTrue(board.setCell(0, 0, 5, false), "Should successfully set cell to a valid number");
    }

    @Test
    void testSetCellInvalid() {
        assertFalse(board.setCell(0, 0, 10, false), "Should reject invalid number");
    }

    @Test
    public void testUndoLastMove() {
        board.setCell(0, 0, 5, false);  // Assuming setCell updates the board and pushes to undoStack
        board.undoLastMove();
        assertEquals(0, board.getCell(0, 0), "Cell should be reset to 0 after undo");
    }
    

    @Test
    void testGetPossibleValues() {
        board.setCell(0, 0, 5, false);
        board.setCell(0, 1, 6, false);
        board.setCell(1, 0, 7, false);
        Set<Integer> expectedValues = Set.of(1, 2, 3, 4, 8, 9);
        assertEquals(expectedValues, board.getPossibleValues(0, 2), "Should return correct possible values");
    }

    @Test
    void testBoardInitialization() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertEquals(0, board.getCell(row, col), "All cells should be initialized to 0");
            }
        }
}
    @Test
    void testLegalMove() {
        board.setCell(0, 0, 5, false);
        assertTrue(board.isLegal(0, 1, 6), "Should allow setting different number in the same row");
    }
    @Test
    void testIllegalMoveSameRow() {
        board.setCell(0, 0, 5, false);
        assertFalse(board.isLegal(0, 1, 5), "Should not allow same number in the same row");
    }

    @Test
    void testIllegalMoveSameColumn() {
        board.setCell(0, 0, 5, false);
        assertFalse(board.isLegal(1, 0, 5), "Should not allow same number in the same column");
    }

    @Test
    void testIllegalMoveSameGrid() {
        board.setCell(0, 0, 5, false);
        assertFalse(board.isLegal(1, 1, 5), "Should not allow same number in the same 3x3 grid");
    }

    @Test
    void testClearAllMoves() {
        board.setCell(0, 0, 5, false);
        board.setCell(0, 1, 6, false);
        board.clearMoves();
        assertTrue(board.getAllMoves().isEmpty(), "All moves should be cleared");
    }

}
