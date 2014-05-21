import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import sun.audio.*;

import javax.imageio.ImageIO;
import javax.swing.*;


////FIX GAME OVER OPTION

public class GameWindow extends JFrame implements ActionListener{
	//Constantss
	public static final int SINGLEPLAYER = 0;
	public static final int PLAYER_AGAINST_AI = 1;
	
	//Global Variable
	private int mode ;
	GamePanel player;
	int score = 0,time = 0;
	private JLabel t,s;
	private JButton reset,close;
	private Timer timer;
	
	TetrisAI ai;
	//Constructor
	public GameWindow(){
		super("Game Window");
		timer = new Timer(1, this);
		timer.start();
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		JPanel btnP = new JPanel();
		close = new JButton("close");
		
		btnP.setLayout(new FlowLayout());
		btnP.add(close);
		close.addActionListener(this);
		
		
		c.add(btnP,BorderLayout.SOUTH);
		
		player = new GamePanel(this);
		
		t = new JLabel("Time Survived: "+ time/3600 +" Hours "+ (time%3600)/60 +" Minutes "+ time%60+" Seconds" );
		s = new JLabel("Score: "+ score);
		
		JPanel topP = new JPanel();
		topP.setLayout(new GridLayout(2,1));
		topP.add(t);
		topP.add(s);
		c.add(topP,BorderLayout.NORTH);
		ai = new TetrisAI(this); //change this to AIGamePanel once we have an AI 
		c.add(ai, BorderLayout.CENTER);
		
		this.setSize(300,650);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
	}
	
	public int getScore(){
		return score;
	}
	
	//Action Handler
	public void resetScoreAI(){
		time = 0;
		score = 0;
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer){
			time += 1;
			ai.callMakeMove();
			t.setText("Time Survived: "+ time/3600 +" min "+ (time%3600)/60 +" sec "+ time%60+" millisec" );
			s.setText("Line: "+ score/10);
		}	
	}
	public void update(){
		repaint();
	}
	
	//Adds score
	public void addScore(int n){
		score += n;
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////// Game Panel Class /////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class GamePanel extends JPanel{
	
	
	//constants
	public static final int ROWS = 20;
	public static final int COLUMBS = 10;
	private final String CHARS = "rbpmocg";
	private final int[] VALUES = {(1),(1),(1),(1),(1),(1),(1)}; //Order :Square,LP,ReverseLP,TShape,ZP,ReverseZP,Line
	private final BufferedImage[] COLORS = new BufferedImage[7]; //Images
	private int[] blocks = {(0),(0),(1),(1),(2),(2),(3),(3),(4),(4),(5),(5),(6),(6)}; //Blocks
	private AudioStream song;
	private InputStream sFile;
	private BufferedImage img;
	protected char[][] table = new char[ROWS][COLUMBS];
	protected CommonPieces current;
	protected GameWindow parent;
	private char color;
	private int linesum = 0;
	
	//constructor
	public GamePanel(GameWindow parent){	
		this.parent = parent;
		this.setSize(300, 600);//set panel size
		
		//store all the images
		try{
			COLORS[0] = ImageIO.read(new File("Blocks/red.jpg"));
			COLORS[1] = ImageIO.read(new File("Blocks/blue.jpg"));
			COLORS[2] = ImageIO.read(new File("Blocks/pink.jpg"));
			COLORS[3] = ImageIO.read(new File("Blocks/gray.jpg"));
			COLORS[4] = ImageIO.read(new File("Blocks/orange.jpg"));
			COLORS[5] = ImageIO.read(new File("Blocks/purple.jpg"));
			COLORS[6] = ImageIO.read(new File("Blocks/green.jpg"));
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Image files not found or cannot be accessed! Game will now exit!", "Fatal Error!", JOptionPane.ERROR_MESSAGE);
		}
		
		//music
		try{
			sFile = new FileInputStream("song.wav");
			song = new AudioStream(sFile);
			AudioPlayer.player.start(song);
		}catch(Exception e){System.out.println("Error when reading music");}
		
		setBackground(Color.BLACK); //set background color
		clearTable(); //clear table
		randomPiece(); 
	}
	
	//resets the game
	public void reset(){
		resetBlocks(); //reset the block
		clearTable(); //clear the table
		randomPiece(); //create random piece
	}
	
	//Initialises the master table at the beginning of each game
	private void clearTable(){
		for (int row = 0 ; row < table.length; row++)
			for (int col = 0 ; col < table[row].length; col++)
				table[row][col] = '.';
	}
	
	//paint override
	@Override
	public void paint(Graphics g){
		super.paint(g);
		for(int row = 0; row< ROWS; row++){
			for(int col = 0; col< COLUMBS; col++){
				if(table[row][col] == '.' && current.getTable()[row][col] == '.'){
					g.setColor(Color.BLACK);
					g.fillRect(col*(getWidth()/COLUMBS), row*(getHeight()/ROWS), getWidth()/COLUMBS, getHeight()/ROWS);
				}
				//if the current block is occupying this space, draw it the color according to the shape of the block
				else if (current.getTable()[row][col] == 'Z' || current.getTable()[row][col] == 'X'){
					ColorChoose(color,g);
					g.drawImage(img,col*(getWidth()/COLUMBS), row*(getHeight()/ROWS), col*(getWidth()/COLUMBS)+30, row*(getHeight()/ROWS) + 30, 0, 0, 30, 30, null);
				//otherwise, draw the images according the the colors saved in the master table
				}else{
					ColorChoose(table[row][col],g);
					g.drawImage(img,col*(getWidth()/COLUMBS), row*(getHeight()/ROWS), col*(getWidth()/COLUMBS)+30, row*(getHeight()/ROWS) + 30, 0, 0, 30, 30, null);
				}
			}
		}
	}
	
	//This is the color chooser used to dye each block a different color
	private void ColorChoose(char color, Graphics g){
		img = COLORS[CHARS.indexOf(String.valueOf(color))];
	}
	
	//when the piece is collided
	public boolean whileCollide(){
		inscribeShape(); //inscribes the fixed shape into the "master" table
		randomPiece(); //get random piece
		while(checkLines()){}//continuously check for completed lines until there are no more
		parent.addScore(linesum);//Adds Score up
		linesum = 0;
		if(current.creatble(table))//if the new generated piece is overlapping with the fixed blocks, game over
			return true;
		return false;
	}
	
	//Function to check if block is going to collide
	public boolean isCollide(){
		for(int row = 0 ; row < ROWS; row++){
			for(int col = 0 ; col < COLUMBS; col++){
				if(current.getTable()[row][col] != '.' &&row == (ROWS -1))
					return true;
				if((current.getTable()[row][col] != '.')&&(table[row+1][col] != '.'))
					return true;
			}
		}
		return false;
	}
	
	//checks and removes completed lines
	private boolean checkLines(){
		int n = 0;
		boolean line;
		//locates the completed line
		for(int row = 0 ; row < table.length; row++){
			line = true;
			for (int col = 0 ; col < table[row].length; col++){
				if(table[row][col] == '.')
					line = false;
			}
			if(line){
				n= row;
				break;
			}
		}
		if(n == 0)
			return false;
		else
			linesum += lineScore(table[n]);
		
		//shifts every line above this line down one line (over writing this line in the process)
		for (int row = n ; row > 0;row--)
			for (int col = 0 ; col < table[row].length; col++)
				table[row][col] = table[row-1][col];

		//goes to the top line and set it to blank
		for(int col = 0; col < table[0].length; col++)
			table[0][col] = '.';
		return true;
	}
	
	//Inscribes the fixed shape into the master table
	private void inscribeShape(){
		for(int row  = 0 ; row < table.length ; row++)
			for (int col = 0 ; col < table[row].length ; col++)
				if(current.getTable()[row][col] == 'Z' || current.getTable()[row][col] == 'X')
					table[row][col] = color;
	}
	
	//Selects a random piece from the seven available pieces
	private void randomPiece(){
		int choice = chooseNum();
		switch(choice){
		 case 0:
			 current = new Square();
			 color = 'r';
			 break;
		 case 1:
			 current = new LP(); 
			 color = 'b';
			 break;
		 case 2:
			 current = new ReverseLP(); 
			 color = 'p';
			 break;
		 case 3:
			 current = new TShape();
			 color = 'o';
			 break;
		 case 4:
			 current = new ZP();
			 color = 'm';
			 break;
		 case 5:
			 current = new ReverseZP();
			 color = 'c';
			 break;
		 case 6:
			 current = new Line();
			 color = 'g';
			 break;
		}
	}
	
	//Chooses a number from the block array
	private int chooseNum(){
		boolean isEmpty = true;
		for (int index = 0; index < blocks.length; index++)
			if(blocks[index] != -1)
				isEmpty = false;
		if(isEmpty){
			resetBlocks();
			randomize();
		}
		boolean valid = false;
		int choosen = -1;
		while (!valid){
			int n = (int)(Math.random()*14);
			if (blocks[n]!=-1){
				valid = true;
				choosen = blocks[n];
				blocks[n] = -1;
			}
		}
		return choosen;		
	}
	
	//Resets the blocks array{
	private void resetBlocks(){
		for (int index = 0; index < blocks.length; index++)
			blocks[index] = index/2;
	}
	
	//Method to randomize the blocks array
	private void randomize(){
		int temp;
		for(int index = 0 ; index < blocks.length; index++){
			temp = blocks[index];
			int n = (int)(Math.random()*14);
			blocks[index] = blocks[n];
			blocks[n] = temp;
			
		}
	}
	
	//Checkes the score
	private int lineScore(char[] a){
		int score = 0;
		for(int index = 0; index < a.length; index++)
			score += VALUES[CHARS.indexOf(String.valueOf(a[index]))];
		return score;
	}
}
