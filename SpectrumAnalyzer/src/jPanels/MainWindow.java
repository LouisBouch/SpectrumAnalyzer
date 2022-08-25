package jPanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import plotting.Plot;
import soundProcessing.AudioPlayback;
import soundProcessing.AudioSettingsWindow;
import tools.ScreenSizeTool;
import wavParsingAndStoring.WavInfo;
import wavParsingAndStoring.WavReader;

public class MainWindow extends JPanel {

	private static final long serialVersionUID = 9006568226542522515L;

	private String col;
	private String row; 
	
	private JPanel panel = this;
	
	private WavReader wavFileInfo;
	
	private ArrayList<Plot> plots = new ArrayList<>();

	private AudioSettingsWindow audioSettings;//The settings window
	private AudioPlayback audio;//Contains the samples to be played

	private boolean fileOpened = false;


	/**
	 * Creates the window that contains the wave information
	 */
	public MainWindow() {
		setBounds(0, 0, ScreenSizeTool.WIDTH , ScreenSizeTool.HEIGHT - 63);	
		setBackground(new Color(80, 85, 110));
		setLayoutDim();
		setLayout(new MigLayout("", col, row));
		setAudio();

		Plot waveFormPlot = new Plot();
		plots.add(waveFormPlot);
		add(waveFormPlot, "cell 7 0 1 5");

		Plot spectrumAnalyzerPlot = new Plot();
		plots.add(spectrumAnalyzerPlot);
		add(spectrumAnalyzerPlot, "cell 7 5 1 5");

		InfoPanel wavInfoPanel = new InfoPanel();
		add(wavInfoPanel, "cell 0 0 7 5");

		JButton btn_getFile = new JButton("Search for wav file");
		btn_getFile.setFocusable(false);
		add(btn_getFile, "cell 1 6 2 1, grow");

		JButton btn_startPause = new JButton("Start/Pause");
		btn_startPause.setPreferredSize(new Dimension(100, 0));
		btn_startPause.setFocusable(false);
		add(btn_startPause, "cell 1 5");

		JButton btn_stop = new JButton("Stop");
		btn_stop.setFocusable(false);
		add(btn_stop, "cell 2 5");
		
		JButton btn_audioSet = new JButton("Audio settings");
		btn_stop.setFocusable(false);
		add(btn_audioSet, "cell 1 9, bottom");

		//Listeners
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println(e.getX() + ", " + e.getY());
			}
		});
		//Gets a file and prepare the necessary information to analyze it
		btn_getFile.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean error = false;
				byte[] binary = null;
				
				FileDialog dialog = new FileDialog( (Frame) null, "Select file to open");
				dialog.setMode(FileDialog.LOAD);
				dialog.setVisible(true);
				
				String selectedFile = dialog.getDirectory() + dialog.getFile();
				
				try {
					File file = new File(selectedFile);
					binary = new byte[(int) file.length()];
					FileInputStream in = new FileInputStream(file);
					in.read(binary);
					in.close();	
				}
				catch (IOException ex) {
					error = true;
					System.out.println(ex);

				}
				if (!error) {
					if (!"WAVE".equals( "" + (char)binary[8] + (char)binary[9] + (char)binary[10] + (char)binary[11])) {
						JOptionPane.showMessageDialog(panel, "The chosen file is not a valid .WAV file");
					}
					else {
						wavFileInfo = new WavReader(binary, dialog.getFile());
						wavInfoPanel.setText(wavFileInfo.getInfoReservoir().toString());
						
						WavInfo infoResservoir = wavFileInfo.getInfoReservoir();
						stop();
						audio.reInitialize(infoResservoir, audioSettings.getClip(), infoResservoir.getDataInfo().getData());
						
//						plots.get(0).setWavInfo(infoResservoir);
						plots.get(0).loadWave(infoResservoir.getDataInfo().getChannelSeparatedData(), 
								infoResservoir.getFormatInfo().getSampleRate(), 
								infoResservoir.getFormatInfo().getChannelsLocationLongName());
						
						fileOpened = true;
					}
				}
			}
		});
		//Starts the media player
		btn_startPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startPause();
			}
		});
		//Stops progression
		btn_stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		//Opens the settings window
		btn_audioSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (audioSettings != null) {
					audioSettings.activate();
				}
			}
		});
	}//End mainWindow
	
	/**
	 * Start, pauses and resumes audio
	 */
	public void startPause() {
		Plot plot = plots.get(0);
		if (fileOpened) {
			if (!audio.isPlaying()) {
				if (audio.getClip().getMicrosecondPosition() == 0) plot.setPlayBackSpeed(audio.getPlayBackSpeed());
				audio.play();
				plot.setAudio(audio);
				plot.start();
			}
			else {
				audio.pause();
				plot.pause();
			}
		}
	}
	/**
	 * Stops audio
	 */
	public void stop() {
		if (audio.getClip().isOpen()) {
			audio.stop();
		}
		plots.get(0).stop();
	}
	/**
	 * Sets the dimension for the layout
	 */
	public void setLayoutDim() {
		col = "[][][][][][][]";
		row = "[][][][][][50px::][][][][]"; 	
	}
	/**
	 * Creates the settings window
	 */
	public void setAudio() {
		audio = new AudioPlayback();
		audioSettings = new AudioSettingsWindow(audio);
	}
}
/*
JButton btnNewButton = new JButton("New button");
btnNewButton.setBounds(1919, 10, 89, 23);
add(btnNewButton);

JButton btnNewButton2 = new JButton("New button");
btnNewButton2.setBounds(0, 1016, 89, 23);
add(btnNewButton2);
 */