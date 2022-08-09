package jPanels;

import java.awt.Color;
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
import subchunksAndInfo.WavInfo;
import tools.ScreenSize;

public class WavFileAnalyzerMAINPANEL extends JPanel {

	private static final long serialVersionUID = 9006568226542522515L;

	private String col;
	private String row; 
	
	private JPanel panel = this;
	
	private WavInfo wavFileInfo;
	
	private ArrayList<Plot> plots = new ArrayList<>();


	/**
	 * Creates the window that contains the wave information
	 */
	public WavFileAnalyzerMAINPANEL() {		
		setBounds(0, 0, ScreenSize.width , ScreenSize.height - 63);	
		setBackground(new Color(80, 85, 110));
		setLayoutDim();
		setLayout(new MigLayout("", col, row));

		Plot waveFormPlot = new Plot();
		plots.add(waveFormPlot);
		add(waveFormPlot, "cell 7 0 1 5");

		Plot spectrumAnalyzerPlot = new Plot();
		plots.add(spectrumAnalyzerPlot);
		add(spectrumAnalyzerPlot, "cell 7 5 1 5");

		WavInfoPanel wavInfoPanel = new WavInfoPanel();
		add(wavInfoPanel, "cell 0 0 7 5");

		JButton btn_getFile = new JButton("Search for wav file");
		btn_getFile.setFocusable(false);
		add(btn_getFile, "cell 1 6");

		JButton btn_start = new JButton("Start");
		btn_start.setFocusable(false);
		add(btn_start, "cell 1 8, grow");

		JButton btn_stop = new JButton("Stop");
		btn_stop.setFocusable(false);
		add(btn_stop, "cell 2 8");

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
						wavFileInfo = new WavInfo(binary, dialog.getFile());
						wavInfoPanel.setText(wavFileInfo.getFileInfo());
						
						plots.get(0).setWavInfo(wavFileInfo);
						plots.get(0).loadWave();
						
						repaint();
					}
				}
			}
		});
		//Starts the media player
		btn_start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				for (int o = 0; o < 100; o++) {
					byte[] bits = ByteManipulationTools.decimalToBits(o);
					for (int i = 0; i < bits.length; i++) {
						System.out.print(bits[i]);
					}
					System.out.println();
				}
				*/
			}
		});
		

	}
	/**
	 * Sets the dimension for the layout
	 */
	public void setLayoutDim() {
		col = "[][][][][][][]";
		row = "[][][][][][50px::][][][][]"; 	
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