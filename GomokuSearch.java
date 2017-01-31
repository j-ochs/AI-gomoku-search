
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
public class GomokuSearch {

	private Socket gomokuSocket;				// socket for communicating w/ server
	private static PrintWriter gridOut;         // takes care of output stream for sockets
	private BufferedReader gridIn;				// bufferedreader for input reading
	private static final int GOMOKUPORT = 17033;
	Random r = new Random();					//PRNG
	protected String gameStatus;
	protected String firstRow = "";
	protected String yourTurn = "";
	protected ArrayList<String> myList = new ArrayList<String>();
	protected String[][]board;					//2d array of game board data (rows, colemns)
	protected int size = myList.size();
	protected Map<String, Integer> weightMap;
	protected Boolean bool = true;
	protected int score = 0;

	public GomokuSearch(String h, int p){
		registerWithGrid(h,p);
	}


	/**
	 * registerWithGrid: takes a string h and an int p
	 * and creates a socket with the specified network name and port number
	 * PRE: h is the name of the machine on the network, p is the port number of the server socket
	 * POST: socket connects with the server socket on the given host
	 */
	public void registerWithGrid(String h, int p) {
		try {
			// connects to h machine on port p
			gomokuSocket = new Socket(h, p);

			// create output stream to communicate with grid
			gridOut = new PrintWriter(gomokuSocket.getOutputStream(), true); 
			//			gridOut.println("0 3"); // send move to server
			//			gridOut.println("0 6");

			//buffered reader reads from input stream from grid
			gridIn = new BufferedReader(new InputStreamReader(gomokuSocket.getInputStream()));
			System.out.println("Successfully Connected to Gomoku Grid Server");
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + h);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + h);
			System.exit(1);
		}
	}




	//potential game status:  ‘continuing’, ‘win’, ‘lose’, ‘draw’ ‘forfeit-move’, or ‘forfeit-time’
	/**
	 * getGameStatus: reads the game data from the server: that is, the (1)game status, (2)lines of 
	 * grid game data[ie rows and colemns], and (3) player whose turn it is.
	 */
	public void getGameStatus(){
		try {
			myList.clear();
			String status = gridIn.readLine().toLowerCase();
			System.out.println("Game status: " + status);

			if((status.equals("win")) || status.equals("lose") || status.equals("draw") || 
					status.equals("forfeit-move") || status.equals("forfeit-time"))
			{
				System.exit(1);			//terminate the program if the game is over
			}

			String fRow = gridIn.readLine();
			myList.add(fRow);
			for (int i=0; i<fRow.length()-1; i++){
				myList.add(gridIn.readLine());
				if (myList.get(i) == "x" || myList.get(i) == "o" || myList.get(i) == " "){
					System.out.println("Your turn: " + myList.get(i));	
					i++;
				}
			}
			yourTurn = gridIn.readLine();
			System.out.println("Your Turn:" + yourTurn);
			System.out.println("Row/Column Dimensions: " + fRow.length() + "x" + fRow.length());
			System.out.println("-----------------------NEW TURN-----------------------");
			System.out.println("      01234567890");

			for (int i=0; i<=myList.size(); i++){
				System.out.println("Row " + i + ":" + myList.get(i));
			}

			gameStatus = status;
			firstRow = fRow;
			//yourTurn = yTurn;
		}
		catch(Exception e) {}
		//	System.out.println("My2DArray:" + board[3][3]);

	}

	/**
	 * convertToArray: Converts the ArrayList of Strings (that is, the game board) to a two-
	 * dimensional Array, whereby we may evaluate individual grid cells more easily.
	 */
	public void convertToArray(){
		String[][]gameBoard = new String[myList.size()][myList.size()];

		//	System.out.println("firstrow:" + myList.get(0));
		for(int r=0; r<myList.size(); r++){
			for (int c=0; c<myList.size(); c++){
				gameBoard[r][c] = myList.get(r).substring(c,c+1);
			}
		}
		board = gameBoard;
		//System.out.println("My2DArray:" + board[0][0]);
	}



	/**
	 * sendMove: sends a move to the grid server given two coordinates.
	 * @param a is the x coordinate of the move
	 * @param b is the y coordinate of the move
	 */
	public void sendMove(int a, int b){
		String f;
		gridOut.println("" + a + " " + "" + b);
		System.out.println("Move Attempted:" + a + " "+ b);
		/*
		gridOut.println("0 0");
		int myRand = r.nextInt(myList.size()+1);

		gridOut.println("0 6");
		gridOut.println(myRand + " " + myRand);
		myRand = r.nextInt(10);
		*/
	}


	/**
	 * availableMoves: searches through all spots on the game board in search of
	 * empty (is " ") grid spaces.
	 * @return a list of all legal moves in a given game state.
	 */
	public ArrayList<Point> availableMoves(){
		ArrayList<Point> legalMoves = new ArrayList<Point>();
		for (int i=0; i<myList.size(); i++){
			for (int j=0; j<myList.size(); j++){
				if (board[i][j].equals(" ")){
					legalMoves.add(new Point(i,j));
				}
				else {}

			}
		}
		//	System.out.println("tempList" + tempList);
		//	System.out.println("tLSize" + tempList.size());
		return legalMoves;
	}


	/**
	 * sendRandomMove: sends a valid random move to the grid server
	 */
	public void sendRandomMove(){

		ArrayList<Point> temp = availableMoves();
		//System.out.println("availabelMoves:" + temp);
		int avR = r.nextInt(temp.size());

		int randX = (int) availableMoves().get(avR).getX();
		int randY = (int) availableMoves().get(avR).getY();
		gridOut.println(randX + " " + randY);

		System.out.println("randomAttempt:" + randX +" "+ randY);

		//	gridOut.println(myRand + " " + myRand2);
//		System.out.println("Move Attempted:" + (avR) + " " + (avR));
	}



	//heuristic weighting/evaluation: 1-100
		//4 in a row: 90
		//3 in a row: 70
		//2 in a row: 50
		//1 piece alone: 30
		//others: 5,10
	/**
	 * evaluate: the heuristic function to assign weights to each potential move within
	 * a given game state. Evaluates each of the legal moves and searches for patterns
	 * around each move, finally deciding which move is best by calculating a "score"
	 * of each individual move/game state.
	 * Currently, does not search/evaluate diagonal patterns.
	 */
	public void evaluate(){
		ArrayList<String> row = myList;
		String[][]gameBoard = board;
		for (int i=0; i<size; i++){
			for (int j=0; j<size; j++){
				//one piece
				if (gameBoard[i][j].equals(" ") && (gameBoard[i][j+1].equals(yourTurn) || 
						gameBoard[i][j-1].equals(yourTurn) || gameBoard[i+1][j].equals(yourTurn) ||
						gameBoard[i-1][j].equals(yourTurn))){
					weightMap.put(gameBoard[i][j], 30);
					score += 30;
				}
				//two in a row
				else if (gameBoard[i][j].equals(" ") && (gameBoard[i][j+1].equals(yourTurn) &&
						gameBoard[i][j+2].equals(yourTurn)) || (gameBoard[i][j-1].equals(yourTurn) &&
						gameBoard[i][j-2].equals(yourTurn)) || (gameBoard[i+1][j].equals(yourTurn) &&
						gameBoard[i+2][j].equals(yourTurn)) || (gameBoard[i-1][j].equals(yourTurn) &&
						gameBoard[i-2][j].equals(yourTurn))){
					weightMap.put(gameBoard[i][j], 50);
					score+=50;
				}
				//three in a row
				else if (gameBoard[i][j].equals(" ") && (gameBoard[i][j+1].equals(yourTurn) ||
						gameBoard[i][j+2].equals(yourTurn)) || (gameBoard[i][j+3].equals(yourTurn) &&
						gameBoard[i][j-1].equals(yourTurn)) || (gameBoard[i][j-2].equals(yourTurn) &&
						gameBoard[i][j-3].equals(yourTurn)) || (gameBoard[i-1][j].equals(yourTurn) &&
						gameBoard[i-2][j].equals(yourTurn))){
					weightMap.put(gameBoard[i][j], 70);
					score += 70;
				}
				else if (gameBoard.equals(" "))weightMap.put(gameBoard[i][j], 10);
				else {
					weightMap.put(gameBoard[i][j], 5);
					score += 5;
				}
				
			}
		}
	//	System.out.println("Board55:" + board[5][5]);
/*		ArrayList<Point> temp = availableMoves();
		//System.out.println("availabelMoves:" + temp);
		System.out.println("temp:" + temp.size());
		int avR = r.nextInt(temp.size());
		int randX = (int) availableMoves().get(avR).getX();
		int randY = (int) availableMoves().get(avR).getY();
		System.out.println("random first:" + randX + " " +randY);
		gridOut.println(randX + " " + randY);

		if (board[randX][randY].equals(" ")){
			sendMove(randX,randY);
		bool = false;
		}*/
		if (bool.equals(true)){
			sendRandomMove();
			bool = false;
		}
		else if (bool.equals(false)){			//searches row by row and colemn by colemn for matches within score priority
			
			String u = yourTurn;
//			System.out.println("ThisISU:" + yourTurn);
//			System.out.println("UU" + u);
//			System.out.println("THisIsFR" + firstRow);
//			System.out.println("size:" + myList.size());
			
			for (int i=0; i<myList.size(); i++){
				//sendMove(row.get(i).indexOf(i),row.get(i).indexOf(" "));
				//System.out.println("I got this far");
				if (row.get(i).contains(" "+u+u+u+u+" ") || row.get(i).contains(u+u+u+u+" ") ||
						row.get(i).contains(" "+u+u+u+u)){
					sendMove(i,row.get(i).indexOf(" "));
				}
				else if (row.get(i).contains(" "+u+u+u+" ") || row.get(i).contains(u+u+u+" ") ||
						row.get(i).contains(" "+u+u+u)){
					sendMove(i,row.get(i).indexOf(" "));
				}
				else if (row.get(i).contains(" "+u+u+" ") || row.get(i).contains(u+u+" ") ||
						row.get(i).contains(" "+u+u)){
					sendMove(i,row.get(i).indexOf(" "));
				}
				else if (row.get(i).contains(" "+u+" ") || row.get(i).contains(u+" ") ||
						row.get(i).contains(" "+u)){
					sendMove(i,row.get(i).indexOf(" "));
				}
				
				else if (row.get(i).contains(u+u)){
					sendRandomMove();
				}
				else {}
				

			}
			/*
			for (int r=0; r<myList.size(); r++){
				for (int c=0; c<myList.size(); c++){
					if (board[r][c].equals(u) && (board[r][c+1].equals(" ") || 
							board[r][c+1].equals(" "))){
						sendMove(r,c+1);
					}
				}
			}*/
		}
		//	System.out.println("weighted Map:" + weightMap.size());
	}


	/**
	 * Minimax: function for evaluating the game tree of possible game states.
	 * @param board is our game board data
	 * @param depth is an int representing the depth of the tree to be searched
	 * @param alpha is an int representing the a move/ game state to be searched
	 * @param beta is an int representing the b move/ game state to be searched
	 * @return
	 */
	public int minimax(String[][] board, int depth, int alpha, int beta){
        int score;
        String[][] tempBoard = new String[myList.size()][myList.size()];
        ArrayList<Point> moves = availableMoves();

        if(moves.size() == 0 || depth == 0){
         //   int evaluation = evaluate().score();
         //   score = evaluation;
         //   return score;
        }
            score = minimax(tempBoard, depth-1, alpha, beta);
            if(score >= 0){		// max
                if (score > alpha)
                    alpha = score;
                if (alpha >= beta)
                    return alpha;
                return alpha;
            } else {    		// min
                if (score < beta)
                    beta = score;
                if (beta <= alpha)
                    return beta;
                return beta;
            }
        }



	/**
	 * run: iterates through program commands, executing gomoku play and beginning countdown thread.
	 */
	public void run(){
		while(true){
			Thread countdown = new Thread();				//two second play-limit timer
			countdown.start();					//1990 ms

			getGameStatus();
			convertToArray();
			availableMoves();
			//sendMove(5,5);
			//sendRandomMove();
			evaluate();

		}
	}


	/**
	 * main: creates a grid Client with the given host and port number, 
	 * then running program commands.
	 * @param args is a String command line argument
	 */
	public static void main(String[] args){
		GomokuSearch client = new GomokuSearch("localhost", GOMOKUPORT);
		client.run();
	}	
}
