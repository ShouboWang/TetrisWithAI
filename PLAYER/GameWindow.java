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

	
	//Global Variables

	GamePanel player, ai;
	int score = 0,time = 0;
	private JLabel t,s;
	private JButton reset,close;
	private Timer timer;

	//Constructor
	public GameWindow(){
		super("Game Window");
		
		//Initializing timer
		timer = new Timer(1000, this);
		timer.start();
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		JPanel btnP = new JPanel();
		
		//Adding buttons
		reset = new JButton("reset");
		close = new JButton("close");
		
		btnP.setLayout(new FlowLayout());
		btnP.add(reset);
		btnP.add(close);
		reset.addActionListener(this);
		close.addActionListener(this);
		
		
		c.add(btnP,BorderLayout.SOUTH);
		

		//Initialize gamepanel
		player = new GamePanel(this);
		//reset.addKeyListener(player);
		
		t = new JLabel("Time Survived: "+ time/3600 +" Hours "+ (time%3600)/60 +" Minutes "+ time%60+" Seconds" );
		s = new JLabel("Score: "+ score);
		
		JPanel topP = new JPanel();
		topP.setLayout(new GridLayout(2,1));
		topP.add(t);
		topP.add(s);
		c.add(topP,BorderLayout.NORTH);

		c.add(player,BorderLayout.CENTER);
		this.setSize(300, 650);
		
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.addKeyListener(player);
	}
	
	//Returns the score kept by this window
	public int getScore(){
		return score;
	}
	
	//Action Handler (for timer)
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == reset){
			player.reset();
			time = 0 ;
			score = 0;
		}else if (e.getSource() == close){
			this.dispose();
		}else if(e.getSource() == timer){
			time += 1;
			t.setText("Time Survived: "+ time/3600 +" Hours "+ (time%3600)/60 +" Minutes "+ time%60+" Seconds" );
			s.setText("Score: "+ score);
		}	
	}
	
	//Adds score
	public void addScore(int n){
		score += n;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////// Game Panel Class /////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class GamePanel extends JPanel implements KeyListener, ActionListener{
	//constants
	public static final int ROWS = 20;
	public static final int COLUMBS = 10;
	
	//Value of each piece
	private final String CHARS = "rbpmocg"; //letters
	private final int[] VALUES = {(10),(10),(10),(10),(10),(10),(10)};//Order :Square,LP,ReverseLP,TShape,ZP,ReverseZP,Line
	private final BufferedImage[] COLORS = new BufferedImage[7]; //images
	private int[] blocks = {(0),(0),(1),(1),(2),(2),(3),(3),(4),(4),(5),(5),(6),(6)};//blocks
	private AudioStream song;
	private InputStream sFile;
	private FileIO readfile = new FileIO();

	//Global Variables
	Timer drop = new Timer(500,this);
	BufferedImage img;
	char[][] table = new char[ROWS][COLUMBS];
	CommonPieces current;
	GameWindow parent;
	char color;
	int linesum = 0;
	//End of Global Variables
	
	
	public GamePanel(GameWindow parent){
		this.parent = parent;
		
		randomPiece();
		
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
		
		try{
			sFile = new FileInputStream("song.wav");
			song = new AudioStream(sFile);
			AudioPlayer.player.start(song);
		}catch(Exception e){
			System.out.println("Error");
			e.printStackTrace();
		}
		setBackground(Color.BLACK);
		drop.start();
		clearTable();
		this.setSize(300, 600);
		this.setFocusable(true);
		this.addKeyListener(this);
		
	}
	//resets the game
	public void reset(){
		drop.stop();
		drop.start();
		resetBlocks();
		clearTable();
		randomPiece();
		this.setFocusable(true);

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
	public void ColorChoose(char color, Graphics g){
		img = COLORS[CHARS.indexOf(String.valueOf(color))];
	}

	//Key Event Handling
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			if(!isCollide())
				current.shiftDown(table);
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			current.shiftRight(table);
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			current.shiftLeft(table);
		if(e.getKeyCode() == KeyEvent.VK_UP)
			current.rotateCW(table);
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
			while(!isCollide())
				current.shiftDown(table);
		repaint();
	}

	//inherited abstract method
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	public void actionPerformed(ActionEvent e) {//only the timer is using this action listener, thus comparing e.getSource() to drop is not necessary
		char[][] oldTable = new char[ROWS][COLUMBS];//table before shift
		for(int i = 0; i< ROWS; i++)
			for(int ii = 0; ii< COLUMBS; ii++)
				oldTable[i][ii] = current.getTable()[i][ii];
		if(isCollide()){// comparing table before shift to table after shift
			inscribeShape(); //inscribes the fixed shape into the "master" table
			randomPiece();//Selects a new random piece
			int lines = 0;
			while(checkLines())//continuously check for completed lines until there are no more
				lines++;
			parent.addScore( lines * linesum);//Adds Score up
			linesum = 0;
			if(current.creatble(table)){
				String name = JOptionPane.showInputDialog(null, "Game Over! Please enter your name: ", "Game Over", JOptionPane.INFORMATION_MESSAGE);//game over dialogue
				readfile.addScore(name, parent.getScore());
				drop.stop();//stops the timer
				parent.dispose();
			}
		}
		current.shiftDown(table);
		repaint();
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


