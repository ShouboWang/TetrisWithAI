import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
//This is the testing class
public class Main {
	public static void main(String Args[]){
		MainWindow game = new MainWindow();
	}
}


//This is the main menu
@SuppressWarnings("serial")
class MainWindow extends JFrame implements ActionListener, KeyListener{
	JButton start,instructions,highscores,quit;
	FileIO fileread = new FileIO();
	public MainWindow(){
		super("Main Menu");
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.setBackground(Color.BLACK);
		
		JPanel mainB = new JPanel();
		mainB.setLayout(new BorderLayout());
		JLabel bImage = new JLabel(new ImageIcon("tetris.jpg"));
		mainB.add(bImage,BorderLayout.CENTER);
		this.addKeyListener(this);
		
		
		JPanel btnP = new JPanel();
		btnP.setLayout(new GridLayout(4,1));
		
		start = new JButton("Start");
		instructions = new JButton("Instructions");
		highscores = new JButton("HighScores");
		quit = new JButton("Quit");
		
		
		btnP.add(start);
		btnP.add(instructions);
		btnP.add(highscores);
		btnP.add(quit);
		mainB.add(btnP,BorderLayout.SOUTH);
		
		start.addActionListener(this);
		instructions.addActionListener(this);
		highscores.addActionListener(this);
		quit.addActionListener(this);
		
		c.add(mainB,BorderLayout.CENTER);
		
		
		this.setSize(300, 550);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
	}
	
	//Action Handlers for the buttons
	
	public void actionPerformed(ActionEvent e) {
		if(((JButton)(e.getSource())).getText().equals("Start")){
			GameWindow gg = new GameWindow();
		}else if(((JButton)(e.getSource())).getText().equals("Instructions")){
			JOptionPane.showMessageDialog(null, fileread.ins(), "Instructions", JOptionPane.INFORMATION_MESSAGE);
		}else if(((JButton)(e.getSource())).getText().equals("HighScores")){
			JOptionPane.showMessageDialog(null, fileread.highScore(), "HighScores", JOptionPane.INFORMATION_MESSAGE);
		}else{
			System.exit(0);
		}
	}
	

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	
}


