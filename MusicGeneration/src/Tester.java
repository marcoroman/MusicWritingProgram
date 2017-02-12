import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Tester {

	public static void main(String[] args) throws FileNotFoundException {
		
		int[] seed = new int[16];
		int[] tension = new int[4];
		int value;
		
		//Seed melody read from text file containing 16 integer values
		System.out.println("Time signature 16/16; reading seed melody from file.");
		
		File file = new File("input.txt");
		Scanner reader = new Scanner(file);
		
		for(int i = 0; i < 16; ++i){
			seed[i] = reader.nextInt();
		}
		
		reader.close();
		reader = new Scanner(System.in);
		
		System.out.println("Four measures of music will be generated.\n" +
							"The first measure will have a tension value of 0.\n");
		
		//Allowing the user to input desired tension values
		for(int i = 1; i < 4; ++i){
			while(true){
				System.out.print("Please input tension for measure " + (i + 1) + " (-7 to 7): ");
				value = reader.nextInt();
				
				if(!(value > 7 || value < -7)){
					tension[i] = value;
					break;
				}
			}
		}
		
		//Creating the tension model object:
		//First measure populated by seed melody
		//Tension array populated by user input
		//Volume array initially set to zero
		TensionModel tm = new TensionModel(seed, tension);
		
		generateMusic(tm);
		
		tm.printVolume();
		tm.printMIA();
		tm.printTension();
		
		reader.close();
	}
	
	//Runs through the algorithm on drive to create music
	//M is iterating variable that keeps track of the measure
	public static void generateMusic(TensionModel tm){
		int difference = 0;
		
		//For our purposes, the a simple form of the algorithm executes over 4 measures
		//(See Drive for full description of algorithm
		for(int M = 1; M < 4; ++M){
			tm.replicateMeasure(M);
			
			difference = tm.getTension(M) - tm.getTension(M - 1);
			
			tm.modifyMeasure(M, difference);
			tm.setVolume(M, (float)((float)(2.0 * difference) / 7));
		}
	}
}