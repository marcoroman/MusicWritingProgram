import java.applet.Applet;
import java.io.*;
import java.applet.AudioClip;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
	
	private static ArrayList<Clip>[] softClips = (ArrayList<Clip>[])new ArrayList[5];
	

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
	private static int counter = 0;
	private static String folderName[] = {"soft", "mediumSoft","medium","mediumLoud","loud"};
	private Clip drums;

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
		
		for (int i = 0;i<5;i++)
		{
			softClips[i] = new ArrayList<>();
		}
		try{
			for(int i = 1; i<22; i++)
			{
			
			for (int j = 0;j<5;++j){
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(new File("C:\\EclipseWorkspace64\\Synthia\\sounds\\"+folderName[j]+"\\" + i + ".wav")));
				softClips[j].add(clip);
				Thread.sleep(clip.getMicrosecondLength()/1000);
			}
		
			}
			System.out.println(softClips[4].size());
		} catch(Exception e) {
			//error
		}
		
	}

	/**
	 * Create the application.
	 */
	
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
		//(See Drive for full description of algorithm)
		for(int M = 1; M < 4; ++M){
			tm.replicateMeasure(M);
			tm.replicateVolume(M);
			
			difference = tm.getTension(M) - tm.getTension(M - 1);
			
			tm.modifyMeasure(M, difference);
			tm.setVolume(M, (float)((float)(2.0 * difference) / 7));
		}
	}
	
	
	public void start() throws IOException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException{
		
		timer = new Timer();
		initializeTimerTask();
		timer.schedule(timerTask, 0, 1000);
		drums = AudioSystem.getClip();
		drums.open(AudioSystem.getAudioInputStream(new File("C:\\EclipseWorkspace64\\Synthia\\sounds\\d.wav")));
		drums.loop(Clip.LOOP_CONTINUOUSLY);
		drums.start();
		Thread.sleep(drums.getMicrosecondLength()/1000);
	}
	
	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				
				//Random rng = new Random();
				//int randomPosition = rng.nextInt(softClips.size());
				int pitch = tm.getMIA(counter/16, counter%16);
				//if(pitch >=21)
				//	pitch = 20;
				//if(currentPosition==randomPosition)
				//softClips.get(randomPosition).setMicrosecondPosition(0);
				
				int volume = tm.getVolume(counter/16)+2;
				System.out.println("Volume value is " + volume);
				
				System.out.println(pitch + " size:" + softClips[volume].size());
				if (pitch == 0){
					if (softClips[volume].get(currentPosition).isRunning())
					{
						softClips[volume].get(currentPosition).setMicrosecondPosition(0);
						softClips[volume].get(currentPosition).stop();
					}
				}
				else if (pitch != 25){
					softClips[volume].get(pitch-1).setMicrosecondPosition(0);
					softClips[volume].get(pitch-1).start();
					if (softClips[volume].get(currentPosition).isRunning())
					{
						softClips[volume].get(currentPosition).setMicrosecondPosition(0);
						softClips[volume].get(currentPosition).stop();
					}
					currentPosition = pitch-1;
				}
				counter++;
				if (counter >= 4*16)
				{
					drums.setMicrosecondPosition(0);
					drums.stop();
					timer.cancel();
					counter = 0;
				}
				   
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
				
				try {
					procedure();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(softClips[4].size()!= 21){
					JOptionPane.showMessageDialog(null, "Loading");
					System.out.println(softClips[0].size() +" " +softClips[1].size() +" "+softClips[2].size() +" "+softClips[3].size() +" "+softClips[4].size());
				}else
				{
					try {
						start();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnsupportedAudioFileException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					JOptionPane.showMessageDialog(null, "Beginning Tension is " + beginningTension + "\nMiddle Tension is " + middleTension + "\nEnding Tension is " + endingTension);
				}
			}
		});
		btnGenerateplay.setFont(new Font("Good Times Rg", Font.BOLD, 16));
		btnGenerateplay.setBounds(62, 156, 299, 113);
		frame.getContentPane().add(btnGenerateplay);
	}
}
