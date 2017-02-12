import java.io.*;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
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
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JTextArea;

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
	private static int[] seed = new int[16];
	private static TensionModel tm;
	private static int counter = 0;
	private String[] value = new String[16];
	private static String folderName[] = {"soft", "mediumSoft","medium","mediumLoud","loud"};
	private Clip drums;
	private static boolean userUsingCustomSeed = false;

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

				tension = new int[4];
				int value;
				
				//if user does not use custom seed values
				//Seed melody read from text file containing 16 integer values
				if(!userUsingCustomSeed)
				{
					System.out.println("Time signature 16/16; reading seed melody from file.");
				
					File file = new File("input.txt");
					Scanner reader = new Scanner(file);
				
					for(int i = 0; i < 16; ++i){
						seed[i] = reader.nextInt();
					}
				
					reader.close();
					//reader = new Scanner(System.in);
				}
				//end of if  
				//else use seed values that user input
				
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
		timer.schedule(timerTask, 0, 00);
		drums = AudioSystem.getClip();
		drums.open(AudioSystem.getAudioInputStream(new File("C:\\EclipseWorkspace64\\Synthia\\sounds\\d.wav")));
		drums.loop(Clip.LOOP_CONTINUOUSLY);
		drums.start();
		Thread.sleep(drums.getMicrosecondLength()/1000);
	}
	
	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {

				int pitch = tm.getMIA(counter/16, counter%16);
				
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
					if (softClips[volume].get(currentPosition).isRunning())
					{
						softClips[volume].get(currentPosition).setMicrosecondPosition(0);
						softClips[volume].get(currentPosition).stop();
					}
					softClips[volume].get(pitch-1).start();
					
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
		frame.setBounds(100, 100, 450, 405);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblNewLabel = new JLabel("Inputs");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblNewLabel.setBounds(27, 48, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		lblBeg = new JLabel("Beginning Tension");
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
		btnGenerateplay.setBounds(62, 191, 299, 113);
		frame.getContentPane().add(btnGenerateplay);
		
		JButton btnAddCustomSeed = new JButton("Add Custom Seed (Optional)");
		btnAddCustomSeed.setFont(new Font("Good Times Rg", Font.BOLD, 12));
		btnAddCustomSeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JTextField field1 = new JTextField();
				JTextField field2 = new JTextField();
				JTextField field3 = new JTextField();
				JTextField field4 = new JTextField();
				JTextField field5 = new JTextField();
				JTextField field6 = new JTextField();
				JTextField field7 = new JTextField();
				JTextField field8 = new JTextField();
				JTextField field9 = new JTextField();
				JTextField field10 = new JTextField();
				JTextField field11 = new JTextField();
				JTextField field12 = new JTextField();
				JTextField field13 = new JTextField();
				JTextField field14 = new JTextField();
				JTextField field15 = new JTextField();
				JTextField field16 = new JTextField();
				Object[] message = {
				    "Seed value 1:", field1,
				    "Seed value 2:", field2,
				    "Seed value 3:", field3,
				    "Seed value 4:", field4,
				    "Seed value 5:", field5,
				    "Seed value 6:", field6,
				    "Seed value 7:", field7,
				    "Seed value 8:", field8,
				    "Seed value 9:", field9,
				    "Seed value 10:", field10,
				    "Seed value 11:", field11,
				    "Seed value 12:", field12,
				    "Seed value 13:", field13,
				    "Seed value 14:", field14,
				    "Seed value 15:", field15,
				    "Seed value 16:", field16,
				};
				int option = JOptionPane.showConfirmDialog(null, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    value[0] = field1.getText();
				    value[1] = field2.getText();
				    value[2] = field3.getText();
				    value[3] = field4.getText();
				    value[4] = field5.getText();
				    value[5] = field6.getText();
				    value[6] = field7.getText();
				    value[7] = field8.getText();
				    value[8] = field9.getText();
				    value[9] = field10.getText();
				    value[10] = field11.getText();
				    value[11] = field12.getText();
				    value[12] = field13.getText();
				    value[13] = field14.getText();
				    value[14] = field15.getText();
				    value[15] = field16.getText();
				
				
				boolean errorExists = false;
				
			try
			{	
				for(int i = 0; i<seed.length; i++)
				{
					if(Integer.parseInt(value[i]) > 0 && Integer.parseInt(value[i]) <= 7)
					{
					seed[i] = Integer.parseInt(value[i]) + 7;
					}
					else if(Integer.parseInt(value[i])==25)
						seed[i] = 25;
					else if(Integer.parseInt(value[i])==0)
						seed[i] = 0;
					else
						errorExists = true;
				}
				
				if(!errorExists)
				userUsingCustomSeed = true;
				else
					JOptionPane.showMessageDialog(null, "User input error. \nNot all values are integers 0 to 7. \nDefault values will be used.");
				
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "User input error. \nNot all values are integers 0 to 7. \nDefault values will be used.");
				}
			}
				
			}
		});
		btnAddCustomSeed.setBounds(61, 148, 300, 32);
		frame.getContentPane().add(btnAddCustomSeed);
		
		JTextArea txtAuthors = new JTextArea();
		txtAuthors.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAuthors.setText(" Created by Thomas Dijulio, Marco Roman, Daniel Yap,\n         Duy Nguyen, and Abraham Yepremian.");
		txtAuthors.setBounds(0, 315, 434, 52);
		frame.getContentPane().add(txtAuthors);
		
	}
}
