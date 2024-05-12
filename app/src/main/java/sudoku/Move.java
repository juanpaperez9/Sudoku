package sudoku;

import java.util.Map;
import java.util.HashMap;

public class Move
{
    public final int row;
    public final int col;
    public final int value;

    private static Map<String, Move> moveCache = new HashMap<>();

    private static String key(int row, int col, int value)
    {
        return row + "," + col + "," + value;
    }

    public static Move valueOf(int row, int col, int value)
    {
        String key = key(row, col, value);
        Move move = moveCache.get(key);
        if (move == null)
        {
            move = new Move(row, col, value);
            moveCache.put(key, move);
        }
        return move;
    }

    private Move(int row, int col, int value)
    {
        this.row = row;
        this.col = col;
        this.value = value;
    }

}
