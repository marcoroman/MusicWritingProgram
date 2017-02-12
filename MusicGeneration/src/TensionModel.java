
public class TensionModel {
	
	private float[] volume = {0, 0, 0, 0};
	//Note: MIA format is MIA[measure #][16th not in measure]
	private int[][] MIA = new int[4][16];
	private int[] tension;
	
	//Class constructor:
	//Sets volume of all measures to 0 (medium)
	//Reads in seed melody from user (file) and sets first measure in MIA
	//Reads in tension values for each measure from user
	public TensionModel(int[] seedMelody, int[] t){
		
		for(int i = 0; i < seedMelody.length; ++i){
			MIA[0][i] = seedMelody[i];
		}
		
		tension = t;
	}
	
	//Prints volume array
	public void printVolume(){
		System.out.println("\nPrinting volume array:");
		
		for(int i = 0; i < volume.length; ++i){
			System.out.print(volume[i] + " ");
		}
		
		System.out.println();
	}
	
	//Prints Measure Information Array
	public void printMIA(){
		System.out.println("\nPrinting Measure Information Array:");
		
		for(int i = 0; i < 4; ++i){
			System.out.print("Measure " + (i + 1) + ": ");
			
			for(int j = 0; j < 16; ++j){
				System.out.print(MIA[i][j] + " ");
			}
			
			System.out.println();
		}
	}
	
	////Prints tension array
	public void printTension(){
		System.out.println("\nPrinting tension array:");
		
		for(int i = 0; i < tension.length; ++i){
			System.out.print(tension[i] + " ");
		}
		
		System.out.println();
	}
	
	//Allows the volume array to be modified
	public void setVolume(int i, float level){
		volume[i] += Math.round(level);
	}
	
	//Sets measure i in MIA to measure i - 1
	public void replicateMeasure(int measure){
		for(int i = 0; i < 16; ++i){
			MIA[measure][i] = MIA[measure - 1][i];
		}
	}
	
	//Increments/decrements the selected measure by a value determined by differences in tension
	public void modifyMeasure(int measure, int increment){
		for(int i = 0; i < 16; ++i){
			if(!(MIA[measure][i] == 0 || MIA[measure][i] == 25))
					MIA[measure][i] += increment;
		}
	}
	
	//Returns a single user-specified value from tension array
	public int getTension(int i){
		return tension[i];
	}
}