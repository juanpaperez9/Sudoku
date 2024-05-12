# Sudoku using JavaFX

## TODO: notes for you to implement
1. loadBoard() method should throw an exception if the file is not a valid sudoku board **DONE**
1. when saving: check if the file already exists, and ask the user if they want to overwrite it**DONE**
1. Undo the last move
    * requires a way to store a stack of moves**DONE**
1. Undo, show values entered: show all the values we've entered since we loaded the board **DONE** there is a bug when is correct move, it outlogs again same move into invalid move, check
1. Hint, Show Hint: highlight all cells where only one legal value is possible **DONE**
1. on right-click handler: show a list of possible values that can go in this square **Done**

## Also add two interesting features of your own
* This is for the final 10 points to get to 100. 
    * If your definition of "interesting" is "the minimum I can do to finish this assignment", then you may end up with A- instead of A. Try for something genuinely interesting.

   * Im adding background music as the first interesting feature **Done**
   * Make a mistake counter. Also a scoreboard, and each correct move is a 50 amount of points. **Done** 
   this way the players will have track of which ones he got correct or incorrect without having to look to on the terminal. Making it more practical
I also created some boards with different difficulties so that users can play depending how good they are
