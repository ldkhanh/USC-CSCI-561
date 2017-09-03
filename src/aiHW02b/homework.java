package aiHW02b;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class homework {
	public static void main(String[] args) {
		new GangGame("input.txt");
	}
	
	public  void Run(String args) {
		new GangGame(args);
	}
}

class GangGame {
	private Game game;
	private Player player;
	private AgentMode algoMode;
	private int N;
	private String Mode, Play;
	private int depth;
	private GameBoard board;
	private boolean inputS;

	public GangGame() {
	}

	public GangGame(String file) {
		inputS = input(file);
		if (inputS) {
			player = Player.getPlayer(Play);
			algoMode = getMode(Mode, depth);
			game = new Game(board, player);
			game.setBoard(board);
			game.setTurn(player);
			game.setMode(player, algoMode);
			game.implement();

			// Write output file
			output(game.getNextMove() + " " + game.getMoveType() + "\n", board.toString());

		}
	}
	private boolean input(String fileName) {
		BufferedReader br = null;
		try {
			String line, lValue[];
			br = new BufferedReader(new FileReader(fileName));
			// ----Get board size-----
			if ((line = br.readLine()) != null)
				line = line.replaceAll("\\s", "");
			else {
				System.out.println("Invalid input");
				return false;
			}
			if (line.matches("[0-9]+") && line.length() > 0)
				N = Integer.parseInt(line);
			else
				return false;
			if (N > 26) {
				System.out.println("Invalid input Size");
				return false;
			}
			// ----board size-----

			Mode = br.readLine().replaceAll("\\s", "");
			Play = br.readLine().replaceAll("\\s", "");
			// ----Get depth -----
			if ((line = br.readLine()) != null)
				line = line.replaceAll("\\s", "");
			else {
				System.out.println("Invalid input");
				return false;
			}
			if (line.matches("[0-9]+") && line.length() > 0)
				depth = Integer.parseInt(line);
			else
				depth = 1; // Depth node should evaluation later

			// ----depth-----

			board = new GameBoard(N);
			// --- Make board
			// ----Extract value -----
			for (int i = 0; i < N; i++) {
				line = br.readLine();
				lValue = line.split(" ");
				for (int j = 0; j < lValue.length; j++) {
					int iPoint = Integer.parseInt(lValue[j]);
					board.setCell(i, j);
					board.getCell(i, j).setValue(iPoint);
				}
			}
			// ----Extract state-----
			for (int i = 0; i < N; i++) {
				line = br.readLine();
				for (int j = 0; j < line.length(); j++) {
					String stateV = Character.toString(line.charAt(j));
					board.getCell(i, j).setState(stateV);
					if (line.charAt(j) != '.') {
						board.addPlPoint(Player.getPlayer(board.getCell(i, j).getState()),
								board.getCell(i, j).getValue());
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void output(String move, String boardState) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			writer.write(move);
			writer.write(boardState);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static AgentMode getMode(String algoMode, int depth) {
		switch (algoMode) {
		case "MINIMAX":
			return new MinimaxMode(depth);
		case "ALPHABETA":
			return new AlphaBetaMode(depth);
		default:
			throw new IllegalArgumentException("Invalid input");
		}
	}
}

class Cell implements Cloneable{
	private final int i,j;
	private int value;
	private String state;
	
	public Cell(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	public Cell(int i, int j, int value, String state) {
		this.i = i;
		this.j = j;
		this.value = value;
		this.state = state;
	}
	
	@Override
	public Cell clone() {
		return new Cell(this.i, this.j, this.value, this.state);
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		if (value < 1 || value > 99)
			this.value = 0;
		else 
			this.value = value;
	}
	
	public String getPos(){
		return (char)(j+65) + Integer.toString(i + 1);
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getX(){
		return this.i;
	}
	public int getY() {
		return this.j;
	}
}

class Action {
	
	private Cell actions;
	private int score = Integer.MIN_VALUE;
	
	public Action(){}
	public Action(Cell action) {
		setActions(action);
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Cell getActions() {
		return actions;
	}
	public void setActions(Cell actions) {
		this.actions = actions;
	}
}

enum Player {

	PLAYERX("X"),
	PLAYERO("O");
	
	private final String state;
	Player(String state) {
		this.state = state;
	}
	
	public Player other() {
		return (this == PLAYERX) ? PLAYERO : PLAYERX;
	}
	
	public static Player getPlayer(String pl) {
		return pl.equals("X") ? PLAYERX : PLAYERO;
	}
	public String getState() {
		return this.state;
	}
	public String getCellPos(Cell cell) {
		return this.state + cell.getPos();
	}
	public boolean isPlayer(String pl) {
		return this.state.equals(pl.charAt(0));
	}
}

class GameBoard implements Cloneable {

	private final Cell[][] board;
	private final int n; // size of board
	private int plPoint = 0;
	private int opPoint = 0;

	public GameBoard(int n) {
		this.n = n;
		this.board = new Cell[this.n][this.n];
		this.plPoint = 0;
		this.opPoint = 0;
	}
	public GameBoard(Cell[][] board, int plPoint, int opPoint) {
		this.n = board[0].length;

		this.board = new Cell[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				this.board[i][j] = board[i][j].clone();
			}
		}
		this.plPoint = plPoint;
		this.opPoint = opPoint;
	}

	@Override
	public GameBoard clone() {
		return new GameBoard(board.clone(), this.plPoint, this.opPoint);
	}
	public boolean equals(GameBoard b) {
		return Arrays.equals(this.board, b.board);
	}

	public void setCell(int i, int j) {
		// remember check valid cell
		Cell cell = new Cell(i, j);
		board[i][j] = cell;
	}

	public Cell getCell(int i, int j) {
		// reminder check valid cell;
		return board[i][j];
	}

	public Cell[][] getBoard() {
		return this.board;
	}

	public boolean isComplete() {
		for (int i = 0; i < getSize(); i++)
			for (int j = 0; j < getSize(); j++) {
				if (this.board[i][j].getState().equals("."))
					return false;
			}
		return true;
	}

	public int getSize() {
		return n;
	}

	public void setOccupied(Player player, Cell cell) {
		int i = cell.getX();
		int j = cell.getY();
		if (i >= this.n || j >= this.n || i < 0 || j < 0)
			throw new IllegalArgumentException("Invalid cell = " + cell.getPos());
		board[i][j].setState(player.getState());
		addPlPoint(player, board[i][j].getValue());
	}

	public int getPlPoint(Player player) {
		if (player == Player.PLAYERX)
			return this.plPoint;
		else
			return this.opPoint;
	}

	public void addPlPoint(Player player, int point) {
		if (player == Player.PLAYERX) {
			this.plPoint += point;
		} else {
			this.opPoint += point;
		}
	}

	public void subtractPlPoint(Player player, int point) {
		if (player == Player.PLAYERX)
			this.plPoint -= point;
		else
			this.opPoint -= point;
	}

	public Player getWinner() {
		if (this.getPlPoint(Player.PLAYERX) == this.getPlPoint(Player.PLAYERO))
			return null;
		else if (this.getPlPoint(Player.PLAYERX) > this.getPlPoint(Player.PLAYERO))
			return Player.PLAYERX;
		else
			return Player.PLAYERO;
	}

	public List<Cell> getValidMove(Player player) {
		List<Cell> moves = new ArrayList<>();
		for (int i = 0; i < getSize(); i++)
			for (int j = 0; j < getSize(); j++) {
				if (isValidMove(board[i][j])) {
					moves.add(board[i][j]);
				}
			}
		return moves;
	}

	public boolean isValidMove(Cell cell) {
		if (cell != null) {
			int x = cell.getX();
			int y = cell.getY();
			if (x < 0 || (x >= this.n) || (y < 0) || (y >= this.n) || !cell.getState().equals("."))
				return false;
			else
				return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();

		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				str.append(this.board[i][j].getState());
			}
			if (i != getSize() - 1)
				str.append("\n");
		}
		return str.toString();
	}
}

class TakeMove {

	private Game game;

	public TakeMove(Game game) {
		this.game = game;
	}

	public void move(Action move) {

		Cell square = move.getActions();
		Player player = game.getTurn();
		GameBoard board = game.getBoard();

		if (!board.isValidMove(square)) {
			throw new IllegalArgumentException("Invalid action");
		}

		if (isRaid(board, player, square.getX(), square.getY())) {
			doRaid(board, player, square);
		} else {
			doStake(board, player, square);
		}

		if (player == Player.PLAYERX) {
			game.setTurn(Player.PLAYERO);
		} else {
			game.setTurn(Player.PLAYERX);
		}
	}

	public boolean isRaid(GameBoard board, Player player, int x, int y) {
		String up = (x - 1 < 0) ? "." : board.getCell(x - 1, y).getState();
		String down = (x + 1 >= board.getSize()) ? "." : board.getCell(x + 1, y).getState();
		String left = (y - 1 < 0) ? "." : board.getCell(x, y - 1).getState();
		String right = (y + 1 >= board.getSize()) ? "." : board.getCell(x, y + 1).getState();

		if (up.equals(player.getState()) || down.equals(player.getState()) || left.equals(player.getState())
				|| right.equals(player.getState())) {
			return true;
		} else {
			return false;
		}
	}

	public void doStake(GameBoard board, Player player, Cell square) {
		board.setOccupied(player, board.getCell(square.getX(), square.getY()));
		this.game.setMoveType("Stake");
		this.game.setNextMove(square.getPos());
	}

	public void doRaid(GameBoard board, Player player, Cell square) {
		int i = 0;
		int x = square.getX();
		int y = square.getY();
		board.setOccupied(player, board.getCell(x, y));
		String up = (x - 1 < 0) ? "." : board.getCell(x - 1, y).getState();
		String down = (x + 1 >= board.getSize()) ? "." : board.getCell(x + 1, y).getState();
		String left = (y - 1 < 0) ? "." : board.getCell(x, y - 1).getState();
		String right = (y + 1 >= board.getSize()) ? "." : board.getCell(x, y + 1).getState();

		if (up.equals(player.other().getState())) {
			board.setOccupied(player, board.getCell(x - 1, y));
			board.subtractPlPoint(player.other(), board.getCell(x - 1, y).getValue());
			i++;
		}
		if (down.equals(player.other().getState())) {
			board.setOccupied(player, board.getCell(x + 1, y));
			board.subtractPlPoint(player.other(), board.getCell(x + 1, y).getValue());
			i++;
		}
		if (left.equals(player.other().getState())) {
			board.setOccupied(player, board.getCell(x, y - 1));
			board.subtractPlPoint(player.other(), board.getCell(x, y - 1).getValue());
			i++;
		}
		if (right.equals(player.other().getState())) {
			board.setOccupied(player, board.getCell(x, y + 1));
			board.subtractPlPoint(player.other(), board.getCell(x, y + 1).getValue());
			i++;
		}
		if (i != 0) {
			this.game.setMoveType("Raid");
			this.game.setNextMove(square.getPos());
		} else {
			this.game.setMoveType("Stake");
			this.game.setNextMove(square.getPos());
		}
	}

}
class Game {
	private GameBoard board;
	private AgentMode playerM = null;
	private AgentMode opponentM = null;
	private Player turn = Player.PLAYERX;
    private String moveType;
    private String nextMove;
	
	public Game() {}
	public Game(GameBoard board, Player turn) {
		this.setBoard(board);
		this.setTurn(turn);
	}
	public GameBoard getBoard() {
		return board;
	}
	public void setBoard(GameBoard board) {
		this.board = board;
	}
	public Player getTurn() {
		return turn;
	}
	public void setTurn(Player turn) {
		this.turn = turn;
	}
	public Player getWinner() {
		return board.getWinner();
	}
	
	public void move(final Action move) {
		new TakeMove(this).move(move);		
	}
	
	// Play the game until it is done.
	public void play(){
		while (!board.isComplete()){
			implement();
		}
	}
	// Play each turn
	public void implement() {
		Action thisMove;		
		if (turn == Player.PLAYERX) {
			thisMove = playerM.getMove(board, Player.PLAYERX);
		} else {
			thisMove = opponentM.getMove(board, Player.PLAYERO);
		}
		if (!board.isComplete() )
			move(thisMove);
	}
	public void setMode(Player player, AgentMode mode) {
		if (player == Player.PLAYERX)
			this.playerM = mode;
		else 
			this.opponentM = mode;
	}
	public String getMoveType() {
		return moveType;
	}
	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}
	public String getNextMove() {
		return nextMove;
	}
	public void setNextMove(String nextMove) {
		this.nextMove = nextMove;
	}
}

interface AgentMode {
	public Action getMove(GameBoard board, Player player);
}

class MinimaxMode implements AgentMode {

	private int maxD;
	private Player currPlayer;

	public MinimaxMode(int depth) {
		this.maxD = depth;
	}

	@Override
	public Action getMove(GameBoard board, Player player) {
		this.currPlayer = player;
		Action move = maxValue(board, player, 0, "root");
		
		return move;
	}

	private Action maxValue(GameBoard board, Player player, int depth, String previousState) {
		if (board.isComplete() || depth >= maxD && !player.isPlayer(previousState)) {
			Action move = new Action();
			move.setScore(score(board, currPlayer));
			return move;
		}
		
		Action maxMove = new Action();
		maxMove.setScore(Integer.MIN_VALUE);
		String maxType = "Stake";
				
		for (Cell cell : board.getValidMove(player)) {			
			Action move = new Action(cell);		
			Game game = new Game(board.clone(), player);
			game.move(move);

			int newDepth = depth + (player.isPlayer(previousState) ? 0 : 1); 
			if (game.getTurn() == player) {
				Action nextMove = maxValue(game.getBoard(), player, newDepth, player.getCellPos(cell));
				move.setActions(nextMove.getActions());
				move.setScore(nextMove.getScore());
			} else {
				move.setScore(minValue(game.getBoard(), player.other(), newDepth, player.getCellPos(cell)).getScore());
			}
			
			if (maxMove.getScore() < move.getScore()) {
				maxMove = move;
				maxType = game.getMoveType();
			} else if (maxMove.getScore() == move.getScore()) {
				if (maxType.equals("Raid") && game.getMoveType().equals("Stake")){
					maxMove = move;
					maxType = game.getMoveType();
				}	
			}
		}
		return maxMove;
	}

	private Action minValue(GameBoard board, Player player, int depth, String previousState) {
		if (board.isComplete() || depth >= maxD && !player.isPlayer(previousState)) {
			Action move = new Action();
			move.setScore(score(board, currPlayer));
			return move;
		}
		
		Action minMove = new Action();
		minMove.setScore(Integer.MAX_VALUE);
		String minType = "Stake";
		
		for (final Cell cell : board.getValidMove(player)) {			
			Action move = new Action(cell);
			Game game = new Game(board.clone(), player);
			game.move(move);

			int newDepth = depth + (player.isPlayer(previousState) ? 0 : 1);

			if (game.getTurn() == player) {
				Action nextMove = minValue(game.getBoard(), player, newDepth, player.getCellPos(cell));
				move.setActions(nextMove.getActions());
				move.setScore(nextMove.getScore());
			} else {
				move.setScore(maxValue(game.getBoard(), player.other(), newDepth, player.getCellPos(cell)).getScore());
			}
			if (move.getScore() < minMove.getScore()) {
				minMove = move;
				minType = game.getMoveType();
			} else if (minMove.getScore() == move.getScore()) {
				if (minType.equals("Raid") && game.getMoveType().equals("Stake")){
					minMove = move;
					minType = game.getMoveType();
				}
				
			}
		}
		return minMove;
	}
	private int score(GameBoard board, Player player) {
		return board.getPlPoint(player) - board.getPlPoint(player.other());
	}	
}

class AlphaBetaMode implements AgentMode {

	private int maxD;
	private Player currPlayer;
	private int alpha = Integer.MIN_VALUE;
	private int beta = Integer.MAX_VALUE;
	
	public AlphaBetaMode(int depth) {
		this.maxD = depth;
	}
	@Override
	public Action getMove(GameBoard board, Player player) {
		this.currPlayer = player;
		Action move = maxValue(board,player, 0, "root", alpha, beta);
		return move;
	}
	
	private Action maxValue(GameBoard board,Player player,int depth, String previousState, int alpha, int beta)  {
		if (board.isComplete() || depth >= maxD && !player.isPlayer(previousState)) {
			Action move = new Action();
			move.setScore(score(board, currPlayer));
			return move;
		}
				
		Action maxMove = new Action();
		maxMove.setScore(Integer.MIN_VALUE);
		if (depth == 1) {
			alpha = Integer.MIN_VALUE;
			beta = Integer.MAX_VALUE;
		}
		String maxType = "Stake";
		
		for (Cell cell : board.getValidMove(player)) {
			Action move = new Action(cell);		
			Game game = new Game(board.clone(), player);
			game.move(move);
			
			int newDepth = depth + (player.isPlayer(previousState) ? 0 : 1); // check later
			
			if (game.getTurn() == player) {
				Action nextMove = maxValue(game.getBoard(), player, newDepth, player.getCellPos(cell), alpha, beta);
				move.setActions(nextMove.getActions());
				move.setScore(nextMove.getScore());
			} else {
				move.setScore(minValue(game.getBoard(), player.other(), newDepth, player.getCellPos(cell), alpha, beta).getScore());
			}
			
			if (beta <= move.getScore()) {
				return move;
			}
			
			alpha = Math.max(alpha, move.getScore());
			
			if (maxMove.getScore() < move.getScore()) {
				maxMove = move;
				maxType = game.getMoveType();
			} else if (maxMove.getScore() == move.getScore()) {
				if (maxType.equals("Raid") && game.getMoveType().equals("Stake")){
					maxMove = move;
					maxType = game.getMoveType();
				}	
			}
		}
		return maxMove;
	}
	
	private Action minValue(GameBoard board,Player player, int depth, String previousState, int alpha, int beta)  {
		if (board.isComplete() || depth >= maxD && !player.isPlayer(previousState)) {
			Action move = new Action();
			move.setScore(score(board, currPlayer));
			return move;
		}
				
		Action minMove = new Action();
		minMove.setScore(Integer.MAX_VALUE);
		if (depth == 1) {
			alpha = Integer.MIN_VALUE;
			beta = Integer.MAX_VALUE;
		}
		String minType = "Stake";
		
		for (Cell cell : board.getValidMove(player)) {
			Action move = new Action(cell);		
			Game game = new Game(board.clone(), player);
			game.move(move);
			
			int newDepth = depth + (player.isPlayer(previousState) ? 0 : 1); // check later
			
			if (game.getTurn() == player) {
				Action nextMove = minValue(game.getBoard(), player, newDepth, player.getCellPos(cell), alpha, beta);
				move.setActions(nextMove.getActions());
				move.setScore(nextMove.getScore());
			} else {
				move.setScore(maxValue(game.getBoard(), player.other(), newDepth, player.getCellPos(cell), alpha, beta).getScore());
			}
			
			if (alpha >= move.getScore()) {
				return move;
			}
			
			beta = Math.min(beta, move.getScore());
			
			if (move.getScore() < minMove.getScore()) {
				minMove = move;
				minType = game.getMoveType();
			} else if (minMove.getScore() == move.getScore()) {
				if (minType.equals("Raid") && game.getMoveType().equals("Stake")){
					minMove = move;
					minType = game.getMoveType();
				}
			}
		}
		return minMove;
	}	
	private int score(GameBoard board, Player player) {
		return board.getPlPoint(player) - board.getPlPoint(player.other());
	}
}



