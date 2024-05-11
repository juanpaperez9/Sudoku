package sudoku;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sudoku.Board.Move;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.control.CustomMenuItem;
import java.util.Set;
import java.util.stream.Collectors;



    public class Sudoku extends Application {
        private Board board = new Board();
        public static final int SIZE = 9;
        private VBox root;
        private TextField[][] textFields = new TextField[SIZE][SIZE];
        private int width = 800;
        private int height = 800;
        private MediaPlayer backgroundPlayer; 
        private Label mistakesLabel;
        private Label scoreLabel;
        private boolean updatingBoard = false;

        

     //Starting the board, created the grid and setting up the textfields
    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new VBox();
        root.getChildren().add(createMenuBar(primaryStage));
        
        

        GridPane gridPane = new GridPane();
        root.getChildren().add(gridPane);
        gridPane.getStyleClass().add("grid-pane");

                

        // Initialize background music
        initBackgroundMusic();
        backgroundPlayer.setVolume(1.0);
        backgroundPlayer.play(); // Start playing the music when the app starts
        
        // create a 9x9 grid of text fields
        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                textFields[row][col] = new TextField();
                TextField textField = textFields[row][col];
                
                // setting ID so that we can look up the text field by row and col
                // IDs are #3-4 for the 4th row and 5th column (start index at 0)
                textField.setId(row + "-" + col);
                gridPane.add(textField, col, row);
                // using CSS to get the darker borders correct
                if (row % 3 == 2 && col % 3 == 2)
                {
                    // we need a special border to highlight the borrom right
                    textField.getStyleClass().add("bottom-right-border");
                }
                else if (col % 3 == 2) { 
                    // Thick right border
                    textField.getStyleClass().add("right-border");
                }
                else if (row % 3 == 2) { 
                    // Thick bottom border
                    textField.getStyleClass().add("bottom-border");
                }

                // add a handler for when we select a textfield
                textField.setOnMouseClicked(event -> {
                    // toggle highlighting
                    if (textField.getStyleClass().contains("text-field-selected"))
                    {
                        // remove the highlight if we click on a selected cell
                        textField.getStyleClass().remove("text-field-selected");
                    }
                    else
                    {
                        // otherwise 
                        textField.getStyleClass().add("text-field-selected");
                    }
                });

                // add a handler for when we lose focus on a textfield
                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue)
                    {
                        // remove the highlight when we lose focus
                        textField.getStyleClass().remove("text-field-selected");
                    }
                });

                // RIGHT-CLICK handler
                // add handler for when we RIGHT-CLICK a textfield
                // to bring up a selection of possible values
                textField.setOnContextMenuRequested(event -> {
                    // Ensure the background color is highlighted
                    textField.getStyleClass().add("text-field-highlight");
                    
                    // Determine the row and column from the TextField's ID
                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
    
                    // Get possible values for this cell
                    Set<Integer> possibleValues = board.getPossibleValues(r, c);
                    String possibleValuesText = possibleValues.stream()
                                                            .sorted()
                                                            .map(String::valueOf)
                                                            .collect(Collectors.joining(" "));

                    // Create and show the alert with possible values
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Possible values for cell (" + r + ", " + c + ")");
                    alert.setHeaderText(null); 
                    alert.setContentText("Possible values: " + possibleValuesText);
                    alert.showAndWait();

                    // Remove the highlight once done
                    textField.getStyleClass().remove("text-field-highlight");
                });

                // using a listener instead of a KEY_TYPED event handler
                // KEY_TYPED requires the user to hit ENTER to trigger the event
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    
                    if(updatingBoard) {
                        return;
                    }

                    if (!newValue.matches("[1-9]?")) {
                        // restrict textField to only accept single digit numbers from 1 to 9
                        textField.setText(oldValue);
                        return;
                    }

                    

                    String id = textField.getId();
                    String[] parts = id.split("-");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);

                    if (!newValue.isEmpty()) {
                        int value = Integer.parseInt(newValue);
                        boolean result = board.setCell(r, c, value, false); // This now returns a boolean
                        if (result) {
                            if (oldValue.isEmpty() || Integer.parseInt(oldValue) != value) {
                                updateScore(50); // Update score by 50 for a correct move
                            }
                        } else {
                            updateMistakes(1); // Update mistakes count by 1 for an incorrect move
                        }
                    } else {
                        board.setCell(r, c, 0,false); // Handle empty input
                    }
                    

                    
                    if (newValue.length() > 0)
                    {
                        try
                        {
                            System.out.printf("Setting cell %d, %d to %s\n", r, c, newValue);
                            int value = Integer.parseInt(newValue);
                            board.setCell(r, c, value,false);
                            // remove the highlight when we set a value
                            textField.getStyleClass().remove("text-field-selected");
                        }
                        catch (NumberFormatException e)
                        {
                            // ignore; should never happen
                        }
                        catch (Exception e)
                        {
                            // TODO: if the value is not a possible value, catch the exception and show an alert
                            System.out.println("Invalid Value: " + newValue);
                        }
                    }
                    else
                    {
                        board.setCell(r, c, 0,false); //change
                    }
                });
            }
        }

        // add key listener to the root node to grab ESC keys
        root.setOnKeyPressed(event -> {
            System.out.println("Key pressed: " + event.getCode());
            switch (event.getCode())
            {
                // check for the ESC key
                case ESCAPE:
                    // clear all the selected text fields
                    for (int row = 0; row < SIZE; row++)
                    {
                        for (int col = 0; col < SIZE; col++)
                        {
                            TextField textField = textFields[row][col];
                            textField.getStyleClass().remove("text-field-selected");
                        }
                    }
                    break;
                default:
                    System.out.println("you typed key: " + event.getCode());
                    break;
                
            }
        });

        Scene scene = new Scene(root, width, height);

        URL styleURL = getClass().getResource("/style.css");
		String stylesheet = styleURL.toExternalForm();
		scene.getStylesheets().add(stylesheet);
        primaryStage.setTitle("Sudoku");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
        	System.out.println("oncloserequest");
        });
    }

    private void updateMistakes(int increment) {
        String labelText = mistakesLabel.getText();
        int currentMistakes = Integer.parseInt(labelText.substring(labelText.indexOf(": ") + 2));
        mistakesLabel.setText("Mistakes: " + (currentMistakes + increment));
    }
    
    
    private void updateScore(int points) {
        int currentScore = Integer.parseInt(scoreLabel.getText().split(": ")[1]);
        scoreLabel.setText("Score: " + (currentScore + points));
    }
    
 
    private void initBackgroundMusic() {
        URL musicResource = getClass().getResource("/Background.mp3");
        System.out.println("Resource URL: " + musicResource); // Should not be null
        if (musicResource != null) {
            Media backgroundMusic = new Media(musicResource.toString());
            backgroundPlayer = new MediaPlayer(backgroundMusic);
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely

            backgroundPlayer.setOnError(() -> {
                System.out.println("Error with media player: " + backgroundPlayer.getError().getMessage());
            });
    
            backgroundPlayer.setOnReady(() -> {
                System.out.println("MediaPlayer is ready, playing music.");
                backgroundPlayer.play();
            });
    
            backgroundPlayer.setOnPlaying(() -> {
                System.out.println("Music is now playing.");
            });
    
            backgroundPlayer.setOnEndOfMedia(() -> {
                System.out.println("Reached end of media.");
            });
    
            backgroundPlayer.setOnStopped(() -> {
                System.out.println("MediaPlayer has stopped.");
            });

        } else {
            System.out.println("Background music file not found.");
        }
    }

    //creatign the menu bar 
    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
    
        // Create labels for mistakes and score
        mistakesLabel = new Label("Mistakes: 0");
        CustomMenuItem mistakesItem = new CustomMenuItem(mistakesLabel);
        mistakesItem.setHideOnClick(false);
    
        scoreLabel = new Label("Score: 0");
        CustomMenuItem scoreItem = new CustomMenuItem(scoreLabel);
        scoreItem.setHideOnClick(false);
    
        // Status menu for showing game statistics
        Menu statusMenu = new Menu("Status");
        statusMenu.getItems().addAll(mistakesItem, scoreItem);
    
        // Music control menu
        Menu musicMenu = new Menu("Music");
        MenuItem playMusicItem = new MenuItem("Play Music");
        playMusicItem.setOnAction(e -> {
            if (backgroundPlayer != null) {
                backgroundPlayer.play();
            }
        });
    
        MenuItem pauseMusicItem = new MenuItem("Pause Music");
        pauseMusicItem.setOnAction(e -> {
            if (backgroundPlayer != null) {
                backgroundPlayer.pause();
            }
        });
    
        MenuItem stopMusicItem = new MenuItem("Stop Music");
        stopMusicItem.setOnAction(e -> {
            if (backgroundPlayer != null) {
                backgroundPlayer.stop();
            }
        });
    
        musicMenu.getItems().addAll(playMusicItem, pauseMusicItem, stopMusicItem);
    
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem loadMenuItem = new MenuItem("Load from file");
        loadMenuItem.setOnAction(event -> loadFromFile(primaryStage));
        MenuItem saveMenuItem = new MenuItem("Save to text");
        saveMenuItem.setOnAction(event -> saveToFile(primaryStage));
        MenuItem printMenuItem = new MenuItem("Print Board");
        printMenuItem.setOnAction(event -> printBoard());
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> primaryStage.close());
        fileMenu.getItems().addAll(loadMenuItem, saveMenuItem, new SeparatorMenuItem(), printMenuItem, new SeparatorMenuItem(), exitMenuItem);
    
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem undoMenuItem = new MenuItem("Undo");
        undoMenuItem.setOnAction(event -> {
            Move lastMove = board.undoLastMove();
            if (lastMove != null) {
                textFields[lastMove.row][lastMove.col].setText(lastMove.oldValue > 0 ? String.valueOf(lastMove.oldValue) : "");
            }
            updateBoard();
        });
    
        MenuItem showValuesMenuItem = new MenuItem("Show values entered");
        showValuesMenuItem.setOnAction(event -> showAllMoves());
        editMenu.getItems().addAll(undoMenuItem, showValuesMenuItem);
    
        // Hints Menu
        Menu hintMenu = new Menu("Hints");
        MenuItem showHintItem = new MenuItem("Show Hint");
        showHintItem.setOnAction(event -> {
            clearHints();  // Clear previous hints before showing new ones
            showHints();
        });
        hintMenu.getItems().add(showHintItem);
    
        // Adding all menus to the menu bar
        menuBar.getMenus().addAll(fileMenu, editMenu, hintMenu, statusMenu, musicMenu);
    
        return menuBar;
    }
    

    // Method to clear previous hints 
    private void clearHints() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                textFields[row][col].getStyleClass().remove("hint-highlight");
            }
        }
    }


    //Loading the files to the board
    private void loadFromFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("../puzzles"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                System.out.println("Loading...");
                board.startInitialization();  // Signal start of initialization
                board = Board.loadBoard(fis, true);  // Load the board with initialization true
                board.endInitialization();    // Signal end of initialization
                updateBoard();
                System.out.println("Loaded successfully.");
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR, "Unable to load sudoku board from file.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
    
    
    
    
    //Savign the board as a file
    private void saveToFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("../puzzles"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null && confirmOverwrite(primaryStage, file)) {
            try {
                Files.write(file.toPath(), board.toString().getBytes());
                Alert successAlert = new Alert(AlertType.INFORMATION, "The board was successfully saved.", ButtonType.OK);
                successAlert.showAndWait();
            } catch (IOException e) {
                Alert errorAlert = new Alert(AlertType.ERROR, "Failed to save the board: " + e.getMessage(), ButtonType.OK);
                errorAlert.showAndWait();
            }
        }
    }

    //Showing hints of which cells are unique
    private void showHints() {
        List<int[]> hintCells = board.getCellsForHints();
        for (int[] cell : hintCells) {
            int row = cell[0];
            int col = cell[1];
            TextField textField = textFields[row][col];
            textField.getStyleClass().add("hint-highlight"); // make sure to define this style class in your CSS
        }
    }    

    //Confirming that I am overwriting the files when saving them
    private boolean confirmOverwrite(Stage primaryStage, File file) {
        if (file.exists()) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "The file already exists. Do you want to overwrite it?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.YES;
        }
        return true;
    }

        //Method to print the board
    private void printBoard() {
        Alert alert = new Alert(AlertType.INFORMATION, board.toString(), ButtonType.OK);
        alert.showAndWait();
    }

        //Method that updates the board
    private void updateBoard() {
        updatingBoard = true;   
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = textFields[row][col];
                int value = board.getCell(row, col);
                textField.setText(value > 0 ? String.valueOf(value) : "");
            }
        }

        updatingBoard = false;
    }
    //Showing all moves done after loading the file
    private void showAllMoves() {
        List<Move> moves = board.getAllMoves();
        StringBuilder sb = new StringBuilder("All entered values:\n");
        for (Move move : moves) {
            sb.append(String.format("Set cell [%d, %d] from %d to %d\n", move.row, move.col, move.oldValue, move.newValue));
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Values Entered");
        alert.setHeaderText("List of all values entered since the board was loaded:");
        alert.setContentText(sb.toString());
        alert.showAndWait();
}

public static void main(String[] args) {
        launch(args);
    }
}
