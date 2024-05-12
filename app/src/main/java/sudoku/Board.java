package sudoku; 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack; 

public class Board {
    private int[][] board; // The Sudoku board represented as a 2D array.
    private Stack<Move> undoStack = new Stack<>();
    private List<Move> allMoves = new ArrayList<>();
    private boolean initializing;

    public Board() {
        board = new int[9][9]; // Initialize an empty board (all values set to 0 by default).
    }

    public static class Move {  
    final int row;
    final int col;
    final int oldValue;
    final int newValue;

    public Move(int row, int col, int oldValue, int newValue) {
        this.row = row;
        this.col = col;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}

    public void startInitialization() {
        initializing = true;
        allMoves.clear();
        System.out.println("Initialization started and moves cleared.");

    }

    public void endInitialization() {
        initializing = false;
        System.out.println("end initialization");
    }

    // Sets a cell to a specific value after validating it.
    public boolean setCell(int row, int col, int value, boolean initializing) {
        if (initializing) {
            board[row][col] = value;
            return true;  // Directly set the value without validation or recording the move
        }
    
        int oldValue = board[row][col];
        if (value < 1 || value > 9) {
            System.out.println("Invalid move: Value must be between 1 and 9.");
            return false;
        }
    
        if (!isLegal(row, col, value)) {
            System.out.println("Invalid move: Setting cell " + row + ", " + col + " to " + value + " is not allowed.");
            return false;
        }
    
        if (oldValue != value) {
            Move move = new Move(row, col, oldValue, value);
            undoStack.push(move);
            allMoves.add(move);
            System.out.println("Correct move: Setting cell " + row + ", " + col + " to " + value);
        }
        
        board[row][col] = value;
        return true;
    }
    
    
    public Move undoLastMove() {
        if (!undoStack.isEmpty()) {
            Move lastMove = undoStack.pop();
            // Set the cell to its old value
            board[lastMove.row][lastMove.col] = lastMove.oldValue;
            return lastMove;
        }
        return null;
    }
    

    public List<Move> getAllMoves() {
        return allMoves;
    }

    // Loads a Sudoku board from an input stream and validates it.
    public static Board loadBoard(InputStream in,boolean initializing) throws IllegalArgumentException {
        Board board = new Board();
        board.startInitialization();
        Scanner scanner = new Scanner(in);
        int[][] tempBoard = new int[9][9]; // Temporary storage to read the board.

        // Read in the board data
        try {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = scanner.nextInt();
                if (value < 0 || value > 9) {
                    scanner.close();
                    throw new IllegalArgumentException("Board contains values outside acceptable range (0-9).");
                }
                tempBoard[row][col] = value;
            }
        }
        } finally {
            scanner.close();
        }
       

        // Validate the board data to ensure it follows Sudoku rules
        if (!isValidBoard(tempBoard)) {
            throw new IllegalArgumentException("Invalid Sudoku board.");
        }

        // Copy valid board to the Board object
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board.setCell(row, col, tempBoard[row][col],initializing);
            }
        }

        board.endInitialization();
        return board;
        }

    // Validates the board to ensure no duplicates in rows, columns, and 3x3 subgrids
    private static boolean isValidBoard(int[][] tempBoard) {
        Set<Integer> seen; // Set to track seen numbers for validation.

        // Check each row and column for duplicates
        for (int i = 0; i < 9; i++) {
            seen = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                int cell = tempBoard[i][j];
                if (cell != 0 && !seen.add(cell)) {
                    return false; // Found a duplicate in a row.
                }
            }

            seen = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                int cell = tempBoard[j][i];
                if (cell != 0 && !seen.add(cell)) {
                    return false; // Found a duplicate in a column.
                }
            }
        }

        // Check 3x3 subgrids for duplicates
        for (int block = 0; block < 9; block++) {
            seen = new HashSet<>();
            int startRow = block / 3 * 3;
            int startCol = block % 3 * 3;
            for (int row = startRow; row < startRow + 3; row++) {
                for (int col = startCol; col < startCol + 3; col++) {
                    int cell = tempBoard[row][col];
                    if (cell != 0 && !seen.add(cell)) {
                        return false; // Found a duplicate in a 3x3 subgrid.
                    }
                }
            }
        }

        return true; // All checks passed, the board is valid.
    }

    public boolean isLegal(int row, int col, int value) {
    
        // Check the 3x3 square
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (board[r][c] == value) {
                    return false;
                }
            }
        }
        // Check the row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == value || board[i][col] == value) {
                return false;
            }
        }
    
        return true;
    }
    
    // Clear the list of all moves
    public void clearMoves() {
        allMoves.clear(); 
    }

    // Returns the value of a specific cell.
    public int getCell(int row, int col) {
        return board[row][col];
    }

    // Determines if a specific cell already has a value.
    public boolean hasValue(int row, int col) {
        return getCell(row, col) > 0;
    }

    // Computes the set of possible values for a specific cell based on Sudoku rules.
    public Set<Integer> getPossibleValues(int row, int col) {
        Set<Integer> possibleValues = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            possibleValues.add(i);
        }

        // Eliminate impossible values by checking row, column, and 3x3 subgrid.
        for (int c = 0; c < 9; c++) {
            possibleValues.remove(getCell(row, c));
        }
        for (int r = 0; r < 9; r++) {
            possibleValues.remove(getCell(r, col));
        }
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++)
            {
                possibleValues.remove(getCell(r, c));
          }
        }

        return possibleValues;
    }

    public boolean checkAndCompleteGrid(int row, int col) {
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        boolean[] found = new boolean[10]; // Track numbers 1-9; index 0 is unused
    
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                int cellValue = board[r][c];
                if (cellValue < 1 || cellValue > 9 || found[cellValue]) {
                    return false; // Duplicate or invalid value in 3x3 grid
                }
                found[cellValue] = true;
            }
        }
    
        return true; // All numbers 1-9 are correctly placed once
    }
    

    //Find the cells to give hints
    public List<int[]> getCellsForHints() {
        List<int[]> hintCells = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) { // check only empty cells
                    Set<Integer> possibleValues = getPossibleValues(row, col);
                    if (possibleValues.size() == 1) {
                        hintCells.add(new int[]{row, col});
                    }
                }
            }
        }
        return hintCells;
    }
    

    // Generates a string representation of the board, primarily for debugging.
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                sb.append(getCell(row, col));
                if (col < 8) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}



