/*
 * This class is for individual pieces of the block
 * 
 * What the class does
 * piece coordinate
 * shifting
 * rotation
 * Verifying if shiftble 
 * */

class CommonPieces{
	
///////////////////////////////////////////////////////////////////////////
////////////////////ALL THE VARIABLES//////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
	
	private final int ROW = 20;
	private final int COL = 10;
	protected char currentTable[][] = new char[ROW][COL]; //current table for playing
	protected int indiCord[]; //master coordinate
	protected int indiRotate[]; //master rotate
	protected int pieceCord[] = new int[6]; //default position of piece
	protected int rotate[] = new int[3]; //the rotational value of each piece
	protected int test_Rotate[]; //to test rotation
	protected int centralX = 5, centralY = 0; //x = col, y = row
	protected String type; //type of block
	protected char[][] testTable = new char[ROW][COL]; //test table
	
	
///////////////////////////////////////////////////////////////////////////
////////////////////MOVEMENT FOR PIECES??//////////////////////////////////
///////////////////////////////////////////////////////////////////////////

	//shift Left
	public void shiftLeft(char[][] a){
		setTestTable(a);
		if(setTable(centralX-1, centralY))
			centralX--;
	}

	//shift Right
	public void shiftRight(char[][] a){
		setTestTable(a);
		if(setTable(centralX+1, centralY))
			centralX++;
	}
	
	//shift down
	public void shiftDown(char[][] a){
		setTestTable(a);
		if(setTable(centralX, centralY+1))
			centralY++;
	}
	
	//rotate clockwise
	public void rotateCW(char[][] a){
		setTestTable(a);
		test_Rotate = pieceCord;
		for(int index = 0; index< rotate.length; index++)
			rotate[index] -=90;
		
		setRotate();
		if(!setTable(centralX, centralY)){
			pieceCord = test_Rotate;
			for(int index = 0; index<rotate.length; index++)
				rotate[index]+=90;
			setRotate();
		}
	}

///////////////////////////////////////////////////////////////////////////
////////////////////SET UP FOR THE PIECES AND TABLES///////////////////////
///////////////////////////////////////////////////////////////////////////
	public CommonPieces(){
		set(currentTable);
		set(testTable);
	}

	public void reset(){
		centralX = 5;
		centralY = 0;
		for(int index = 0; index< pieceCord.length; index++)
			pieceCord[index] = indiCord[index];
		for(int index = 0; index< rotate.length; index++)
			rotate[index] = indiRotate[index];
		setTable(centralX, centralY);
	}
	
	//makes the table for current table
	public boolean creatble(char[][] table){
		for(int row = 0; row<2; row++)
			for(int col = 0; col<table[row].length; col++)
				if(currentTable[row][col]!='.'&&table[row][col]!='.')
					return true;
		return false;
	}
	
	//getter for table
	public char[][] getTable(){
		return currentTable;
	}

	//set up the test table
	public void setTestTable(char[][] a){
		for(int row = 0; row< ROW; row++)
			for(int col = 0; col< COL; col++){
				testTable[row][col] = a[row][col];
			
			}
	}
	
	//reset the table to blank
	public void set(char[][] setTable){
		for(int row = 0; row< ROW; row++)
			for(int col = 0; col< COL; col++)
				setTable[row][col] = '.';
	}
	
	//setting up the table for the game
	public boolean setTable(int central_X, int central_Y){
		if(checkChangeble(central_Y, central_X))
			return false;
		for(int index = 1; index< 4; index++)
			if(checkChangeble(central_Y-pieceCord[index*2-1], central_X+pieceCord[index*2-2]))
				return false;
		
		//if the table is OK to change, it will change
		set(currentTable);
		currentTable[central_Y][central_X] = 'Z';
		for(int index = 1; index< 4; index++)
			currentTable[central_Y-pieceCord[index*2-1]][central_X+pieceCord[index*2-2]] = 'X';
		return true;
	}
	
	//used to rotate
	public void setRotate(){
		if(type.equals("Line")){
			pieceCord[4] = 2*(int)Math.cos(Math.toRadians(rotate[2]));
			pieceCord[5] = 2*(int)Math.sin(Math.toRadians(rotate[2]));
		}
		else if(!type.equals("Tshape")){
			pieceCord[4] = (int)(Math.round(Math.sqrt(2)*Math.cos(Math.toRadians(rotate[2]))));
			pieceCord[5] = (int)(Math.round(Math.sqrt(2)*Math.sin(Math.toRadians(rotate[2]))));
		}
		else{
			pieceCord[4] = (int)Math.cos(Math.toRadians(rotate[2]));
			pieceCord[5] = (int)Math.sin(Math.toRadians(rotate[2]));
		}
		pieceCord[0] = (int)Math.cos(Math.toRadians(rotate[0]));
		pieceCord[1] = (int)Math.sin(Math.toRadians(rotate[0]));
		pieceCord[2] = (int)Math.cos(Math.toRadians(rotate[1]));
		pieceCord[3] = (int)Math.sin(Math.toRadians(rotate[1]));
		
	}

	//check if the piece is changeable
	public boolean checkChangeble(int row, int col){
		if(row>=20||col>=10||col<=-1||row<0)
			return true;
		if(testTable[row][col]!='.')
			return true;
		return false;
	}
}

///////////////////////////////////////////////////////////////////////////
//////////INDIVIDUAL PIECES AND EXTENDS MASTER CLASS///////////////////////
///////////////////////////////////////////////////////////////////////////

//line piece
class Line extends CommonPieces{ //10
	public Line(){
		indiCord = new int[]{-1,0,1,0,2,0};
		indiRotate = new int[]{180,0,0};
		type = "Line";
		reset();
	}
}

//Z shape
class ZP extends CommonPieces{ //50
	public ZP(){
		indiCord = new int[]{-1,0,0,-1,1,-1};
		indiRotate = new int[]{180,270,315};
		type = "ZP";
		reset();
	}
}

//Reverse Z shape
class ReverseZP extends CommonPieces{ //50
	public ReverseZP(){
		indiCord = new int[]{0,-1,1,0,-1,-1};
		indiRotate = new int[]{270,0,225};
		type = "ReverseZP";
		reset();
	}
}

//square shape
class Square extends CommonPieces{ //20
	public Square(){
		indiCord = new int[]{0,-1,1,0,1,-1};
		indiRotate = new int[]{0,0,0};
		type = "Square";
		reset();
	}
	public void rotateCW(char[][]a){}
}

//L shape
class LP extends CommonPieces{ //35
	public LP(){
		indiCord = new int[]{-1,0,1,0,-1,-1};
		indiRotate = new int[]{180,0,225};
		type = "LP";
		reset();
	}
}

//reverse L shape
class ReverseLP extends CommonPieces{ //35
	public ReverseLP(){
		indiCord = new int[]{-1,0,1,0,1,-1};
		indiRotate = new int[]{180,0,315};
		type = "ReverseLP";
		reset();
	}
}

//T-shape
class TShape extends CommonPieces{ //30
	public TShape(){
		indiCord = new int[]{-1,0,0,-1,1,0};
		indiRotate = new int[]{180,270,0};
		type = "TShape";
		reset();
	}
}