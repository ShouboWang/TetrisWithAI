/**
 * This is the AI for the game, it extends gamePanle class and uses it's methods
 * **/

import java.awt.Graphics;
//import javax.swing.Timer;
import java.io.*;
import java.util.Scanner;

class TetrisAI extends GamePanel{
	
///////////////////////////////////////////////////////////////////////////
//////////VARIABLES FOR AI/////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
	
	//these are the temporary variables in the class
	private final int ROW = 20;
	private final int COL = 10;
	private char [][] currentTable = new char[ROW][COL]; //current playing table
	private double score = 0; //current score
	private int numOfAI = 0; //the position of AI 
	private int[] bestmove = new int[]{-5,3};
	private int pN[] = new int[]{1,-1};
	private double valueList[][] = new double[4][8]; //all of the value of moves for genetic AI
	//pieceWall = 0
	//piececontact = 1
	//height = 2;
	//row = 3
	//hole = 4
	//blockade = 5
	//score = 6
	//the best move = 7
	
///////////////////////////////////////////////////////////////////////////
//////////SET UP FOR AI////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
	
	//constructor
	public TetrisAI(GameWindow parent) {
		super(parent);
		populateList(); //populate the table
	}
	
	//populate the table by reading from file
	public void populateList(){
		try{
			Scanner in = new Scanner (new File("GENE.txt"));
			while(in.hasNext())
				for(int row = 0; row< valueList.length; row++)
					for(int col = 0; col< valueList[row].length; col++)
						valueList[row][col] = in.nextDouble();
			in.close();
		}catch(Exception e){}
	}
	
	//updates the table by combining the two tables
	public void upDateTable(char[][] currentTable, char[][] table){
		for(int i = 0; i< 20; i++)
			for(int ii = 0; ii< 10; ii++)
				this.currentTable[i][ii] = currentTable[i][ii];
		for(int i = 0; i< 20; i++)
			for(int ii = 0; ii< 10; ii++)
				if(table[i][ii]!='.')
					this.currentTable[i][ii] = table[i][ii];
	}
	
	//call to make move
	public void callMakeMove(){
		makeMove();
	}
	
	//reset the AI
	public void resetAI(){
		valueList = sort(valueList);
		for(int index = 0; index< 6; index++){
			int randomRow = (int)(Math.random()*3);
			valueList[3][index] = valueList[randomRow][index]+(Math.random()*getMutation(valueList[randomRow][6])*pN[(int)(Math.random()*2)]);
		}
		print();
		numOfAI = 3;
		writeFile();
	}
	
/////////////////////////////////////////////
///////The following 3 methods are used to sort the array and find the highest score
	public double[][] sort(double[][] table){
		double[][] out = new double[4][8];
		for(int i = 0; i < 3 ; i++){
			copyLine(out[i],table[maxI(table)[0]]);
			out[i][7] = 0;
		}
		return out;		
	}
	public int[] maxI(double[][] table){
		int[] max = {-1,-1};
		for(int index = 0 ; index < 4; index++){
			if(table[index][7] != 1.0 && (table[index][6] > max[1])){
				max[0] = index;
				max[1] = (int)table[index][6];
			}
		}
		table[max[0]][7] = 1.0;
		return max;
	}
	
	public void copyLine(double[] a , double[] b){
		for(int i = 0 ; i < a.length; i++)
			a[i] = b[i];
	}
	
	//write to file
	public void writeFile(){
		String tempLine = "";
		System.out.println("Writting to file");
		try{
			PrintWriter out = new PrintWriter("GENE.txt");
			for(int row = 0; row< valueList.length; row++){
				for(int rowLength = 0; rowLength<valueList[row].length; rowLength++)
					tempLine += valueList[row][rowLength] + " ";
				out.println(tempLine);
				tempLine = "";
			}
			out.close();
		}catch(Exception e ){System.out.println("Error encountered when writing file");}
	}
	
///////////////////////////////////////////////////////////////////////////
//////////THE AI ITSELF////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
	
	public void print(){
		for(int i = 0; i<4; i++){
			for(int ii = 0; ii<8; ii++)
				System.out.print(valueList[i][ii]+" ");
			System.out.println();
		}
	}
	
	//Method that will do all the function as a player would
	public void makeMove(){
		
		//if the number of AI is at max
		if(numOfAI == 4)
			resetAI();
		
		//calls the recursion for AI
		AI(-5,0);
		
		//shifts two down
		current.shiftDown(table);
		current.shiftDown(table);
		
		//rotate
		for(int i = 0; i<bestmove[1]; i++)
			current.rotateCW(table);
		
		//if move left
		if(bestmove[0]<0)
			for(int i = bestmove[0]; i<0; i++)
				current.shiftLeft(table);
		//if move right
		else if(bestmove[0]>0)
			for(int i = 0; i< bestmove[0]; i++)
				current.shiftRight(table);
		//shift all the way down
		while(!isCollide())
			current.shiftDown(table);
		
		//repaint
		parent.repaint();
		
		//extends to extended class and do functions when dead
		if(whileCollide()){
			valueList[numOfAI][6] = parent.getScore()/10; //assign score
			numOfAI++; //AI increases
			reset(); //reset the playing field
			parent.resetScoreAI(); //reset the score
		}
		score = 0; //reset the score
	}
	
	//paint
	public void paintComponent(Graphics g){
		super.paintComponents(g);
	}
	
	//check the score for the table after its combined
	public double checkScore(){
		
		double gridScore = 0; //the score for this grid
		int tempHole = 0; //Temporary hole
		int tempHeight = 0; //temporary height
		boolean ifBlockade = false; //check if its a blockade
		
		for(int col = 9; col>=0; col--){ //goes through all the rows
			for(int row = 19; row>=0; row--){//goes through all the rows
				
				//blockade + hole
				if(currentTable[row][col]!='.'&&ifBlockade)
					gridScore+= valueList[numOfAI][5];
				if (currentTable[row][col]=='.' && row!=0 && currentTable[row-1][col]!='.'){ //setTemp
					gridScore += (tempHole+1)*valueList[numOfAI][4];
					tempHole = 0;
					ifBlockade = true;
				}
				else if(currentTable[row][col]=='.') //setTemp
					gridScore+=tempHole;
				
				//check for height
				if(currentTable[row][col]!='.'&&(20-row)>tempHeight){
					gridScore -= tempHeight*valueList[numOfAI][2];
					gridScore +=(20-row)*valueList[numOfAI][2];
					tempHeight = (20-row);
				}
				
				//check for piece connected and wall connected
				if(currentTable[row][col]!='.'){
					if(row+1<20&&currentTable[row+1][col]!='.')
						gridScore+=valueList[numOfAI][1];
					if(row-1>=0&&currentTable[row-1][col]!='.')
						gridScore+=valueList[numOfAI][1];
					if(col+1<10&&currentTable[row][col+1]!='.')
						gridScore+=valueList[numOfAI][1];
					if(col-1>=0&&currentTable[row][col-1]!='.')
						gridScore+=valueList[numOfAI][1];
					if((row==19&&col==0)||(row==19&&col==9)){
						gridScore+=valueList[numOfAI][0]*2;
					}
					else if(row==19||col==0||col==9){
						gridScore+=valueList[numOfAI][0];
					}
				}
			}
			tempHole = 0;
			ifBlockade = false;
		}
		
		//check for lines formed
		for(int row = 0; row<20; row++)
			for(int col = 0; col<10; col++){
				if(currentTable[row][col]=='.')
					break;
				if(col==9){
					gridScore+=valueList[numOfAI][3];
				}
			}
		return gridScore;
	}

	//the recursion for the AI
	public void AI(int move, int rotate){
		
		if(move>4) //if reach the right end, base case
			return ;
		
		//shift two down
		current.shiftDown(table);
		current.shiftDown(table);
		
		//rotate
		for(int index = 0; index< rotate; index++)
			current.rotateCW(table);
		
		//if more left
		if(move<0)
			for(int index = move; index<0; index++)
				current.shiftLeft(table);
		
		//if move right
		else if(move>0)
			for(int index = 0; index< move; index++)
				current.shiftRight(table);
		
		//shift down
		while(!isCollide())
			current.shiftDown(table);
		
		//update the table
		upDateTable(current.getTable(), table);
		
		//get the score
		double thisScore = checkScore();
		
		//if score is greater, set best move
		if(thisScore>score){
			bestmove[0] = move;
			bestmove[1] = rotate;
			score = thisScore;
		}
		
		//if score is the same, but with less moves, reset the move
		else if (thisScore==score&&Math.abs(0-move)<Math.abs(0-bestmove[0])){
			bestmove[0] = move;
			bestmove[1] = rotate;
			score = thisScore;
		}
		
		//if score is the same, move is the same but with less rotation, reset rotation
		else if(thisScore==score&&Math.abs(0-move)==Math.abs(0-bestmove[0])&&Math.abs(0-rotate)<Math.abs(0-bestmove[1])){
			bestmove[1] = rotate;
			score = thisScore;
		}
		
		//reset the piece table
		current.reset();
		
		//recall itself
		if(rotate==3) //if rotate is 3
			AI(move+1, 0); //move increases, rotate reset
		else
			AI(move, rotate+1); //move remains, rotate increases
	}
	
	private double getMutation(double valueList2){
		if(valueList2<100)
			return 3;
		if(valueList2<300)
			return 2.7;
		if(valueList2<600)
			return 2;
		if(valueList2<1000)
			return 1.8;
		if(valueList2< 1500)
			return 1.3;
		if(valueList2< 2000)
			return 1;
		if(valueList2< 2500)
			return 0.5;
		if(valueList2< 3000)
			return 0.1;
		if(valueList2< 500)
			return 0.05;
		if(valueList2< 7000)
			return 0.01;
		if(valueList2< 10000)
			return 0.001;
		return 0.0001;
	}
}
