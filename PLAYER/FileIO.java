import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.*;

class FileIO {
	
	/**
	 * This class is for high score reading
	 * */
	public String highScore(){
		
		int score; 
		String raw="", playerName, highScore; 
		ArrayList <String> name = new ArrayList <String>();
		
		try{
			Scanner in = new Scanner(new File("DATA/HIGHSCORE.txt")); //get the scanner
			while(in.hasNextLine()){ //read all the file
				raw = in.nextLine();
				if(raw.length() == 0)
					break;
				name.add((raw.substring(raw.indexOf("$")+1))+""+(raw.substring(0,raw.indexOf("$"))));
			}
			in.close();	//close the file
		}catch(FileNotFoundException e){System.out.println("File Is Not Found"); //exception 
		}catch (Exception e){System.out.println("Error when opening file: "+e);} //other exception
		
		Collections.sort(name); //sort the array
		
		highScore = "Name               Score  \n";
		for(int i = name.size()-1; i>=0; i--){
			
			if(i>10)
				break;
			score = Integer.parseInt(checkIsDigit(name.get(i),0));//convert into digit
			playerName = name.get(i).replaceAll(score+"", ""); //get the name
			
			while(playerName.length()<18) //only display name within 18 char
				playerName+="-";
			highScore += "|"+playerName+" "+score+"\n"; 
		}
		return highScore; //return high score
	}
	
	//recursion to check if is it digit
	public String checkIsDigit(String string, int index){
		if(!(string.charAt(index)>='0'&&string.charAt(index)<='9'))
			return "";
		else
			return string.charAt(index)+""+checkIsDigit(string, index+1);
	}
	
	/**
	 * class to add to highscore
	 * */
	public void addScore(String name, int score){
		
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter("DATA/HIGHSCORE.txt",true)); //buffered reader
			out.newLine(); //new line
			out.write(name+"$"+score); //add score
			out.close(); //close
		}catch(FileNotFoundException e){System.out.println("File is not foud");} //error
		catch(Exception e){System.out.println("Unknown error occur when writing to file");}
	}
	
	/*
	 * Get the instruction
	 * */
	public String ins(){
		String ins = "";
		try{
			Scanner in = new Scanner(new File("DATA/INS.txt"));
			while(in.hasNext())
				ins+=in.nextLine()+"\n";
			in.close();
		}catch(FileNotFoundException e){System.out.println("File Not Found");}
		catch(Exception e){System.out.println("Error when opening instruction");}
		return ins;
	}
}
