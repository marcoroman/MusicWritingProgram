import java.applet.Applet;
import java.io.*;
import java.applet.AudioClip;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.util.ArrayList;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Frame1 {

	private JFrame frame;
	private JLabel lblNewLabel;
	private JLabel lblBeg;
	private JLabel lblMiddleTension;
	private JLabel lblEndingTension;
	private JComboBox comboBox1, comboBox2, comboBox3;
	
	private static ArrayList<Clip> softClips;
	

	private String[] options = {"-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7"};
	
	private static int beginningTension;
	private static int middleTension;
	private static int endingTension;
	private JComboBox comboBox_1;
	private JComboBox comboBox;
	private JLabel lblSynthia;
	private JButton btnGenerateplay;
	private Timer timer;
    	private TimerTask timerTask;
    	private int currentPosition;
	private static int[] tension;
	private static TensionModel tm;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		});
		
		
		
		softClips = new ArrayList<>();
		try{
			for(int i = 0; i<7; i++)
			{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File("C:\\EclipseWorkspace64\\Synthia\\sounds\\" + i + ".wav")));
			softClips.add(clip);
			Thread.sleep(clip.getMicrosecondLength()/1000);
			}
		} catch(Exception e) {
			//error
		}
		
	}

	/**
	 * Create the application.
	 */
	
	public void start()
	{
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 16; ++j){
				softClips.get(tm.getMIA(i, j)).start();
			}
		}
	}
	
	
	
	public static void procedure() throws IOException
	{
		//start of tester
				int[] seed = new int[16];
				tension = new int[4];
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
				tension[1] = beginningTension;
				tension[2] = middleTension;
				tension[3] = endingTension;
				
				//Creating the tension model object:
				//First measure populated by seed melody
				//Tension array populated by user input
				//Volume array initially set to zero
				tm = new TensionModel(seed, tension);
				
				generateMusic(tm);
				
				tm.printVolume();
				tm.printMIA();
				tm.printTension();
				
				reader.close();
				
				//end of tester
	}
	

	
	public static void generateMusic(TensionModel tm){
		int difference = 0;
		
		//For our purposes, the a simple form of the algorithm executes over 4 measures
		//(See Drive for full description of algorithm
		for(int M = 1; M < 4; ++M){
			tm.replicateMeasure(M);
			tm.replicateVolume(M);
			
			difference = tm.getTension(M) - tm.getTension(M - 1);
			
			tm.modifyMeasure(M, difference);
			tm.setVolume(M, (float)((float)(2.0 * difference) / 7));
		}
	}
	
	
	public void startRandom(){
		timer = new Timer();
		initializeTimerTask();
		timer.schedule(timerTask, 0, 1000);
	}
	
	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				Random rng = new Random();
				int randomPosition = rng.nextInt(softClips.size());
				//if(currentPosition==randomPosition)
				//softClips.get(randomPosition).setMicrosecondPosition(0);
				softClips.get(randomPosition).start();
				if (softClips.get(currentPosition).isRunning())
				{
					softClips.get(currentPosition).setMicrosecondPosition(0);
					softClips.get(currentPosition).stop();
				}
				currentPosition = randomPosition;   
			}
		};
	}

	
	public Frame1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 353);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblNewLabel = new JLabel("Inputs");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel.setBounds(27, 48, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		lblBeg = new JLabel("Beggining Tension");
		lblBeg.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblBeg.setBounds(27, 55, 174, 49);
		frame.getContentPane().add(lblBeg);
		
		lblMiddleTension = new JLabel("Middle Tension");
		lblMiddleTension.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMiddleTension.setBounds(160, 63, 123, 32);
		frame.getContentPane().add(lblMiddleTension);
		
		lblEndingTension = new JLabel("Ending Tension");
		lblEndingTension.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEndingTension.setBounds(280, 55, 109, 49);
		frame.getContentPane().add(lblEndingTension);
		
		comboBox1 = new JComboBox(options);
		comboBox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource()==comboBox1)
				{
					JComboBox cb = (JComboBox) arg0.getSource();
					String msg = (String) cb.getSelectedItem();
					switch(msg)
					{
					case "-7": beginningTension = -7; break;
					case "-6": beginningTension = -6; break;
					case "-5": beginningTension = -5; break;
					case "-4": beginningTension = -4; break;
					case "-3": beginningTension = -3; break;
					case "-2": beginningTension = -2; break;
					case "-1": beginningTension = -1; break;
					case "0": beginningTension = 0; break;
					case "1": beginningTension = 1; break;
					case "2": beginningTension = 2; break;
					case "3": beginningTension = 3; break;
					case "4": beginningTension = 4; break;
					case "5": beginningTension = 5; break;
					case "6": beginningTension = 6; break;
					case "7": beginningTension = 7; break;
					default: JOptionPane.showMessageDialog(null, "Error"); break;
					}
				}
			}
		});
		comboBox1.setBounds(51, 97, 46, 32);
		comboBox1.setSelectedIndex(7);
		frame.getContentPane().add(comboBox1);
		
		comboBox2 = new JComboBox(options);
		comboBox2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource()==comboBox2)
				{
					JComboBox cb = (JComboBox) arg0.getSource();
					String msg = (String) cb.getSelectedItem();
					switch(msg)
					{
					case "-7": middleTension = -7; break;
					case "-6": middleTension = -6; break;
					case "-5": middleTension = -5; break;
					case "-4": middleTension = -4; break;
					case "-3": middleTension = -3; break;
					case "-2": middleTension = -2; break;
					case "-1": middleTension = -1; break;
					case "0": middleTension = 0; break;
					case "1": middleTension = 1; break;
					case "2": middleTension = 2; break;
					case "3": middleTension = 3; break;
					case "4": middleTension = 4; break;
					case "5": middleTension = 5; break;
					case "6": middleTension = 6; break;
					case "7": middleTension = 7; break;
					default: JOptionPane.showMessageDialog(null, "Error"); break;
					}
				}
			}
		});
		comboBox2.setSelectedIndex(7);
		comboBox2.setBounds(184, 97, 46, 32);
		frame.getContentPane().add(comboBox2);
		
		comboBox3 = new JComboBox(options);
		comboBox3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getSource()==comboBox3)
				{
					JComboBox cb = (JComboBox) arg0.getSource();
					String msg = (String) cb.getSelectedItem();
					switch(msg)
					{
					case "-7": endingTension = -7; break;
					case "-6": endingTension = -6; break;
					case "-5": endingTension = -5; break;
					case "-4": endingTension = -4; break;
					case "-3": endingTension = -3; break;
					case "-2": endingTension = -2; break;
					case "-1": endingTension = -1; break;
					case "0": endingTension = 0; break;
					case "1": endingTension = 1; break;
					case "2": endingTension = 2; break;
					case "3": endingTension = 3; break;
					case "4": endingTension = 4; break;
					case "5": endingTension = 5; break;
					case "6": endingTension = 6; break;
					case "7": endingTension = 7; break;
					default: JOptionPane.showMessageDialog(null, "Error"); break;
					}
				}
			}
		});
		comboBox3.setSelectedIndex(7);
		comboBox3.setBounds(304, 97, 46, 32);
		frame.getContentPane().add(comboBox3);
		
		lblSynthia = new JLabel("Synthia");
		lblSynthia.setFont(new Font("CGF Locust Resistance", Font.BOLD, 18));
		lblSynthia.setBounds(27, 11, 184, 32);
		frame.getContentPane().add(lblSynthia);
		
		btnGenerateplay = new JButton("Generate/Play");
		btnGenerateplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Beginning Tension is " + beginningTension + "\nMiddle Tension is " + middleTension + "\nEnding Tension is " + endingTension);
				try {
					procedure();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				start();
			}
		});
		btnGenerateplay.setFont(new Font("Good Times Rg", Font.BOLD, 16));
		btnGenerateplay.setBounds(62, 156, 299, 113);
		frame.getContentPane().add(btnGenerateplay);
	}
}
