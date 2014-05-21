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
class MainWindow extends JFrame implements ActionListener{
	JButton start,instructions,highscores,quit;
	public MainWindow(){
		super("Main Menu");
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.setBackground(Color.BLACK);
		
		JPanel mainB = new JPanel();
		mainB.setLayout(new BorderLayout());
		JLabel bImage = new JLabel(new ImageIcon("tetris.jpg"));
		mainB.add(bImage,BorderLayout.CENTER);
		
		
		JPanel btnP = new JPanel();
		btnP.setLayout(new GridLayout(4,1));
		
		start = new JButton("Start AI");
		quit = new JButton("Quit");
		
		
		btnP.add(start);
		btnP.add(quit);
		mainB.add(btnP,BorderLayout.SOUTH);
		
		start.addActionListener(this);
		quit.addActionListener(this);
		
		c.add(mainB,BorderLayout.CENTER);
		
		
		this.setSize(300, 550);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
	}

	
	public void actionPerformed(ActionEvent e) {
		if(((JButton)(e.getSource())).getText().equals("Start AI")){
			GameWindow gg = new GameWindow();
			
		}	
		else
			System.exit(0);
		
	}
}


