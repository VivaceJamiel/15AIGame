import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class MyTest {
	
	GameController a = new GameController();
	int[] board = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 , 12, 13, 14, 15};
	 int levelFour[] = {4, 1, 2, 0, 8, 5, 6, 3, 12, 9, 10, 7, 13, 14, 15, 11};
	int[] currBoard;
	int toSwap = -1;
	
	// No Javafx stuff version of function in GameController.
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
	        }
        }
	}

	@Test
	void Array1() throws Exception {
		assertEquals(15, board[a.getArrayVal(3, 3)]);
	}

	@Test
	void Array2() throws Exception {
		assertEquals(0, board[a.getArrayVal(0, 0)]);
	}
	
	@Test
	void AIOne1() throws Exception {
		String heuristic = "heuristicOne";
		currBoard = Arrays.copyOf(levelFour, levelFour.length);
		A_IDS_A_15solver ids = new A_IDS_A_15solver(heuristic, currBoard);
		ArrayList<Node> solution = ids.solutionSteps;
		assertEquals(10, solution.size());
	}
	
	@Test
	void AIOne2() throws Exception {
		String heuristic = "heuristicOne";
		currBoard = Arrays.copyOf(board, board.length);
		A_IDS_A_15solver ids = new A_IDS_A_15solver(heuristic, currBoard);
		ArrayList<Node> solution = ids.solutionSteps;
		assertEquals(1, solution.size());
	}
	
	@Test
	void AITwo1() throws Exception {
		String heuristic = "heuristicTwo";
		currBoard = Arrays.copyOf(levelFour, levelFour.length);
		A_IDS_A_15solver ids = new A_IDS_A_15solver(heuristic, currBoard);
		ArrayList<Node> solution = ids.solutionSteps;
		assertEquals(10, solution.size());
	}
	
	@Test
	void AITwo2() throws Exception {
		String heuristic = "heuristicTwo";
		currBoard = Arrays.copyOf(board, board.length);
		A_IDS_A_15solver ids = new A_IDS_A_15solver(heuristic, currBoard);
		ArrayList<Node> solution = ids.solutionSteps;
		assertEquals(1, solution.size());
	}
	
	@Test
	void swap1() throws Exception {
		currBoard = Arrays.copyOf(board, board.length);
		toSwap = 1;
		int[] state = {1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		swap();
		assertEquals(true, Arrays.equals(state, currBoard));
	}
	
	@Test
	void swap2() throws Exception {
		currBoard = Arrays.copyOf(board, board.length);
		toSwap = 4;
		int[] state = {4, 1, 2, 3, 0, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		swap();
		assertEquals(true, Arrays.equals(state, currBoard));
	}
	
	@Test
	void swap3() throws Exception {
		currBoard = Arrays.copyOf(board, board.length);
		toSwap = 15;
		int[] state = {15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 0};
		swap();
		assertEquals(false, Arrays.equals(state, currBoard));
	}
	
	@Test
	void swap4() throws Exception {
		toSwap = 9;
		int change[] = {1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		currBoard = change;
		int[] state = {1, 2, 3, 4, 5, 9, 6, 7, 8, 0, 10, 11, 12, 13, 14, 15};
		swap();
		assertEquals(true, Arrays.equals(state, currBoard));
	}

}
