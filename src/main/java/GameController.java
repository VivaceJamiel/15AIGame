import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class GameController implements Initializable{

	@FXML
	private AnchorPane root;
	
	@FXML
	private GridPane grid;
	
	@FXML private MenuItem solutionButton;
	
	ExecutorService ex = Executors.newFixedThreadPool(4);
	
	private int[] goalState = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};	//goal state used for comparison

	/* Boards for each level */
	 int levelOne[] = {1, 2, 0, 7, 4, 5, 3, 6, 8, 9, 10, 11, 12, 13, 14, 15};
	
	 int levelTwo[] = {0, 2, 1, 12, 15, 5, 6, 11, 8, 9, 10, 7, 4, 3, 14, 13};
	 
	 int levelThree[] = {2, 1, 12, 0, 15, 5, 6, 11, 8, 9, 10, 7, 4, 3, 14, 13};
	
	 int levelFour[] = {4, 1, 2, 0, 8, 5, 6, 3, 12, 9, 10, 7, 13, 14, 15, 11};
	
	 int levelFive[] = {15, 2, 0, 12, 8, 5, 1, 11, 4, 9, 6, 7, 3, 14, 10, 13};
	
	 int levelSix[] = {8, 15, 2, 12, 4, 5, 1, 11, 3, 9, 6, 7, 0, 14, 10, 13};
	
	 int levelSeven[] = {8, 2, 1, 12, 4, 15, 0, 11, 3, 5, 6, 7, 14, 9, 10, 13};
	
	 int levelEight[] = {4, 8, 1, 12, 3, 2, 15, 11, 14, 5, 6, 7, 9, 10, 13, 0};
	
	 int levelNine[] = {2, 1, 12, 11, 0, 15, 5, 6, 8, 9, 10, 7, 4, 3, 14, 13};
	
	 int levelTen[] = {2, 1, 12, 11, 8, 15, 5, 6, 9, 10, 14, 7, 4, 3, 0, 13};
	
	// Int array to hold the current board
	int currBoard[];  
	
	// Holds the current level as a number, used for random generation.
	int currLevel = 0;  
	
	// Set to hold levels already used, prevents repeated levels until all levels played.
	Set<Integer> visited = new HashSet<Integer>();  
	
    // Holds the solution for when the solution is finished running
	ArrayList<Node> solution;
	
	// Holds the index value for the number to be switched with the 0, the blank space
	int toSwap;  
	
	// If the board is in a win state, helps with animation if solver finished the puzzle
	boolean win = false;  
			
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.setOpacity(0);
		FadeTransition in = new FadeTransition(Duration.millis(1000));
		in.setNode(root);
		in.setFromValue(0);
		in.setToValue(1.0);
		in.play();
		//  Runs the about dialog box after fade in transition ends
		in.setOnFinished(e -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					about();
				}
			});
		});
		currBoard = Arrays.copyOf(levelOne, levelOne.length);
		currLevel = 1;
		visited.add(1);
		
		//  Puts the buttons on the GridPane
		for (int i = 0; i < 16; i++) {
			Button b = new Button();
			b.setMinHeight(140);
			b.setPrefHeight(140);
			b.setPrefWidth(140);
			b.setMinWidth(140);
			b.setStyle("-fx-background-color: #faebd7;-fx-font-size:40");
			
			String t = "";
			if (!(currBoard[i] == 0)) {
				t += currBoard[i];
			}
			b.setText(t);
			b.setOnAction(e -> {
				Button but = (Button) e.getSource();
				if (!(but.getText().equals(""))) {				
			        int row = GridPane.getRowIndex(but);
			        int col = GridPane.getColumnIndex(but);
			        toSwap = getArrayVal(col, row);
			        swap();
				}
			});
			grid.add(b, i%4, i/4);
		}
	}
	
	//  Checks to see if board is in the win state
	public void checkWin() {
//		printArray(currBoard);
		if (Arrays.equals(currBoard, goalState)) {
			winDialog();
		}
	}
	
	//  Dialog box to show how to. 
	public void about() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Welcome to the 15 puzzle!");
		alert.setHeaderText("Objective");
		alert.setContentText("Your goal is to order the tiles by arranging them from 1 to 15 from the top-left with the empty space being first.");
		alert.showAndWait();
		about2();
	}
	
	public void about2() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Instructions");
		alert.setHeaderText("How to Play");
		alert.setContentText("To move tiles, click an adjacent tile to the empty space to slide it into the space.\nYou can also use the AI solver to show the next several moves, located in the \"Game\" menu.\nYou can see the instructions again by going to the \"About\" menu.\nTo start a new game or generate a new puzzle, go to the \"New\" menu.\nTo exit, go to the \"Exit\" menu");
		alert.showAndWait();
	}
	
	//  Dialog for when the board is solved by the player.
	public void winDialog() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Congratulations!");
		alert.setHeaderText("You have solved the puzzle!");
		alert.setContentText("Do you want to start a new puzzle or exit the game?");
		
		ButtonType newPuzzle = new ButtonType("New Puzzle");
		
		ButtonType exit = new ButtonType("Exit Game");
		ButtonType cancel = new ButtonType("Cancel");
		
		alert.getButtonTypes().setAll(newPuzzle, exit, cancel);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == newPuzzle) {
			newLevel();
		} else if (result.get() == exit) {
			exit();
		} else {
			System.out.println("Canceled");
		}
	}
	
	//  Swaps the tile clicked with the empty space if its adjacent
	public void swap() {
        int prevBoard[] = currBoard;
        int zeroInd = -1;
        int temp;
        for (int i = 0; i < 16; i++) {
        	if (currBoard[i] == 0) {
        		zeroInd = i;
        	}
        }
        if ((toSwap == 3 && zeroInd == 4) || (toSwap == 7 && zeroInd == 8) || (toSwap == 11 && zeroInd == 12) || (zeroInd == 3 && toSwap == 4) || (zeroInd == 7 && toSwap == 8) || (zeroInd == 11 && toSwap == 12)) {
        } else {
	        if ((zeroInd == toSwap + 4 || zeroInd == toSwap - 4 || zeroInd == toSwap + 1 || zeroInd == toSwap - 1)) {
		        temp = prevBoard[toSwap];
		        prevBoard[toSwap] = prevBoard[zeroInd];
		        prevBoard[zeroInd] = temp;
		        currBoard = prevBoard;
		        updateBoard();
	        }
        }
		checkWin();
	}
	
	//  Updates the gridpane by taking the values from the currentBoard and changing the tiles to match.
	public void updateBoard() {
		for (int i = 0; i < 16; i++) {
			String t = "";
			t += currBoard[i];
			Button b = (Button) getNode(i/4, i%4);
			if (t.equals("0")) {
				b.setText("");
			} else {
				b.setText(t);
			}
		}
	}
	
	//  Gets the 1d array value from the 2d gridpane value
	public int getArrayVal(int col, int row) {
		if (row > 3 || col > 3) {
			System.out.println("Out of bounds");
		}
		return (row*4)+col;
	}
	
	//  Retrieves the node in the gridpane by row and column
	public javafx.scene.Node getNode(int row, int column) {
		for (javafx.scene.Node node : grid.getChildren()) {
	        if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column){
	            return node;
	        }
	    }
		return null;
	}
	
	//  Debugging purposes, prints the array
	public void printArray(int[] toPrint) {
        for (int i = 0; i < 16; i++) {
	    	System.out.print(toPrint[i] + " ");
	    	if (i == 3 || i == 7 || i == 11) {
	    		System.out.println();
	    	}
        }
        System.out.println("\n");
	}
	
	//  Loads a new level, goes through new levels and doesn't repeat until all levels are visited.
	public void newLevel() {
		Random r = new Random();
		int newLevel = currLevel;
		if (visited.size() == 10) {
			visited.clear();
		}
		while (visited.contains(newLevel)) {
			newLevel = r.nextInt(10) + 1;
		}
		switch (newLevel) {
			case 1:
				currBoard = Arrays.copyOf(levelOne, levelOne.length);
				currLevel = 1;
				visited.add(currLevel);
				break;
			case 2:
				currBoard = Arrays.copyOf(levelTwo, levelTwo.length);
				currLevel = 2;
				visited.add(currLevel);
				break;
			case 3:
				currBoard = Arrays.copyOf(levelThree, levelThree.length);
				currLevel = 3;
				visited.add(currLevel);
				break;
			case 4:
				currBoard = Arrays.copyOf(levelFour, levelFour.length);
				currLevel = 4;
				visited.add(currLevel);
				break;
			case 5:
				currBoard = Arrays.copyOf(levelFive, levelFive.length);
				currLevel = 5;
				visited.add(currLevel);
				break;
			case 6:
				currBoard = Arrays.copyOf(levelSix, levelSix.length);
				currLevel = 6;
				visited.add(currLevel);
				break;
			case 7:
				currBoard = Arrays.copyOf(levelSeven, levelSeven.length);
				currLevel = 7;
				visited.add(currLevel);
				break;
			case 8:
				currBoard = Arrays.copyOf(levelEight, levelEight.length);
				currLevel = 8;
				visited.add(currLevel);
				break;
			case 9:
				currBoard = Arrays.copyOf(levelNine, levelNine.length);
				currLevel = 9;
				visited.add(currLevel);
				break;
			case 10:
				currBoard = Arrays.copyOf(levelTen, levelTen.length);
				currLevel = 10;
				visited.add(currLevel);
				break;
		}
		updateBoard();
		win = false;
		solutionButton.setDisable(true);
	}
	
	//  Restarts the board
	public void restart() {
		currBoard = Arrays.copyOf(levelOne, levelOne.length);
		currLevel = 1;
		visited.clear();
		visited.add(1);
		solutionButton.setDisable(true);
		win = false;
		updateBoard();
	}
	
	//  Exits the application
	public void exit() {
		Platform.exit();
        System.exit(0);
	}
	
	/* --------------------------------------  Animation --------------------------------------   */
	// Uses a timeline to animate the next few moves. Animates the remaining moves if less than 10 moves to the goal
	public void seeNextMoves() {
		Timeline tl = new Timeline();
		int size = solution.size();
		// If the solution is less 10 or less
		if (size < 11) {  
			for (int i = 0; i < size; i++) {
				int index = i;
				Node origin = solution.get(i);
				int[] step = origin.getKey();
				KeyFrame kf = new KeyFrame(Duration.seconds(index + 1),
						event -> {
							currBoard = Arrays.copyOf(step, step.length);
							updateBoard();
						});
				tl.getKeyFrames().add(kf);
			}
			win = true;  // If the solution has less than 10 moves, then the solver will solve the puzzle and win.
		} else {  // animates the next 10 moves, *Note* 0 is the board when solver was called, so 1 - 10 are the next moves
			for (int i = 0; i < 11; i++) {
				int index = i;
				Node origin = solution.get(i);
				int[] step = origin.getKey();
				KeyFrame kf = new KeyFrame(Duration.seconds(index + 1),
						event -> {
							currBoard = Arrays.copyOf(step, step.length);
							updateBoard();
						});
				tl.getKeyFrames().add(kf);
			}
		}
		// Checks if the board is finished after the solver animates the next moves. 
		tl.setOnFinished(e -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (win) {
						currBoard = Arrays.copyOf(goalState, goalState.length);
						solverFinishedPuzzle();
					}
				}
			});
		});
		tl.play();
	}
	
	//  Dialog if the solver finished the puzzle
	public void solverFinishedPuzzle() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Puzzle Solved!");
		alert.setHeaderText("The solver has solved the puzzle!");
		alert.setContentText("Do you want to start a new puzzle or exit the game?");
		
		ButtonType newPuzzle = new ButtonType("New Puzzle");
		ButtonType exit = new ButtonType("Exit Game");
		ButtonType cancel = new ButtonType("Cancel");
		
		alert.getButtonTypes().setAll(newPuzzle, exit, cancel);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == newPuzzle) {
			newLevel();
		} else if (result.get() == exit) {
			exit();
		} else {
			System.out.println("Canceled");
		}
	}
	
	//  Dialog to notify that the solver is done running.
	public void solverDone() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("15 Puzzle Solver");
		alert.setHeaderText("The AI solver has finished its query");
		if (solution.size() < 11) {
			alert.setContentText("To see the next " + solution.size() + " moves, go to the menu option \"Game\" and click \"See next moves\"");
		} else {
			alert.setContentText("To see the next " + 10 + " moves, go to the menu option \"Game\" and click \"See next moves\". You can repeatedly see the moves until you use the solver again");
		}
		alert.showAndWait();
	}
	
	//  Solver to run heuristic 1
	public void runAI1() {
		String ai = "heuristicOne";
		solutionButton.setDisable(true);
		int[] board = currBoard;
		Future<ArrayList<Node>> future = ex.submit(new MyCall(board, ai));
		
		ex.submit(() -> {
			try {
				solution = future.get();
				Platform.runLater(() -> {
					solutionButton.setDisable(false);
					solverDone();
				});
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}	
		});
	}
	
	//  Solver to run heuristic 2
	public void runAI2() {
		String ai = "heuristicTwo";
		solutionButton.setDisable(true);
		int[] board = currBoard;
		Future<ArrayList<Node>> future = ex.submit(new MyCall(board, ai));
		
		ex.submit(() -> {
			try {
				solution = future.get();
				Platform.runLater(() -> {
					solutionButton.setDisable(false);
					solverDone();
				});
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}	
		});
	}
	
	//  Code to run in the separate thread.
	class MyCall implements Callable<ArrayList<Node>> {
		String heuristic;
		int board[];
		
		MyCall(int board[], String ai) {
			this.board = board;
			heuristic = ai;
		}
		
		@Override
		public ArrayList<Node> call() throws Exception {
			System.out.println("Calling future");
			A_IDS_A_15solver ids = new A_IDS_A_15solver(heuristic, board);
			return ids.solutionSteps;
		}
		
	}

}
