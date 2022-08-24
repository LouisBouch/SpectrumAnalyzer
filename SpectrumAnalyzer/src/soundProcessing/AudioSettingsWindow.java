package soundProcessing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class AudioSettingsWindow extends JFrame {
	
	private static final long serialVersionUID = -2170469832705727045L;
	
	private JPanel pane;
	private AudioPlayback audioPlayback;
	
	private Mixer mixer;
	private Line line;
	private Clip clip;

	public AudioSettingsWindow(AudioPlayback audioPlayback) {
		this.audioPlayback = audioPlayback;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setVisible(false);

		setContentPane();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});//End window listener

	}//End AudioSettingsWindow
	public void setContentPane() {
		setResizable(false);
		pane = new JPanel();
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setBackground(new Color(80, 85, 110));
		setContentPane(pane);
		SpringLayout sl_contentPane = new SpringLayout();
		pane.setLayout(sl_contentPane);
		
		Mixer.Info[] playbackDevices = getPlayBackList();
		
		JLabel lbl_mixers = new JLabel("Select mixer: ");
		lbl_mixers.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lbl_mixers.setForeground(new Color(200, 200, 200));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_mixers, 5, SpringLayout.NORTH, pane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_mixers, 10, SpringLayout.WEST, pane);
		pane.add(lbl_mixers);
		
		JComboBox<Mixer.Info> comboBox_playBackDevice = new JComboBox<Mixer.Info>(playbackDevices);
		for (Component comp : comboBox_playBackDevice.getComponents()) {
			if (comp instanceof AbstractButton) comp.setVisible(false);;//Removes drop down button
		}
		comboBox_playBackDevice.setMaximumRowCount(4);
		Dimension originalSize = comboBox_playBackDevice.getPreferredSize();
		comboBox_playBackDevice.setPreferredSize(new Dimension(lbl_mixers.getPreferredSize().width, comboBox_playBackDevice.getPreferredSize().height));
		comboBox_playBackDevice.setFocusable(false);
		setMixerAndLine((Mixer.Info) comboBox_playBackDevice.getSelectedItem());
		comboBox_playBackDevice.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				comboBox_playBackDevice.setPreferredSize(originalSize);
				add(comboBox_playBackDevice);
				sl_contentPane.putConstraint(SpringLayout.NORTH, comboBox_playBackDevice, 5, SpringLayout.SOUTH, lbl_mixers);
				sl_contentPane.putConstraint(SpringLayout.WEST, comboBox_playBackDevice, 10, SpringLayout.WEST, pane);
				revalidate();
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				comboBox_playBackDevice.setPreferredSize(new Dimension(lbl_mixers.getPreferredSize().width, comboBox_playBackDevice.getPreferredSize().height));
				add(comboBox_playBackDevice);
				sl_contentPane.putConstraint(SpringLayout.NORTH, comboBox_playBackDevice, 5, SpringLayout.SOUTH, lbl_mixers);
				sl_contentPane.putConstraint(SpringLayout.WEST, comboBox_playBackDevice, 10, SpringLayout.WEST, pane);
				revalidate();
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				comboBox_playBackDevice.setPreferredSize(new Dimension(lbl_mixers.getPreferredSize().width, comboBox_playBackDevice.getPreferredSize().height));
				add(comboBox_playBackDevice);
				sl_contentPane.putConstraint(SpringLayout.NORTH, comboBox_playBackDevice, 5, SpringLayout.SOUTH, lbl_mixers);
				sl_contentPane.putConstraint(SpringLayout.WEST, comboBox_playBackDevice, 10, SpringLayout.WEST, pane);
				revalidate();
			}
		});
		comboBox_playBackDevice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMixerAndLine((Mixer.Info) comboBox_playBackDevice.getSelectedItem());
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, comboBox_playBackDevice, 5, SpringLayout.SOUTH, lbl_mixers);
		sl_contentPane.putConstraint(SpringLayout.WEST, comboBox_playBackDevice, 10, SpringLayout.WEST, pane);
		comboBox_playBackDevice.setVisible(true);
		add(comboBox_playBackDevice);
		
//		infoMixers();
		
		
	}//End setContentPane
	
	/**
	 * Activates the settings window
	 */
	public void activate() {
		setVisible(true);
	}
	
	/**
	 * Closes the window
	 */
	public void close() {
		setVisible(false);
	}
	/**
	 * Gets the playBack devices
	 * @return Returns the playBack devices available on this PC
	 */
	public Mixer.Info[] getPlayBackList() {
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		
		int nbDevices = 0;
		for (int mixer = 0; mixer < mixers.length; mixer++) {
			if (mixers[mixer].getDescription().equals("Direct Audio Device: DirectSound Playback")) {
				mixers[nbDevices] = mixers[mixer];
				nbDevices++;
			}
		}
		Mixer.Info[] playbackDevices = new Mixer.Info[nbDevices];
		for (int pbDev = 0; pbDev < nbDevices; pbDev++) {
			playbackDevices[pbDev] = mixers[pbDev];
		}
		
		return playbackDevices;
	}
	/**
	 * Sets the mixer and line from the mixer info
	 * @param mixer The mixer info
	 */
	public void setMixerAndLine(Mixer.Info mixerInfo) {
		this.mixer = AudioSystem.getMixer(mixerInfo);
		Line.Info[] linesInfo = mixer.getSourceLineInfo();
		//Gets clip line
		for (Line.Info lineInfo : linesInfo) {
			if (lineInfo.getLineClass().isAssignableFrom(Clip.class)) {
				try {
					line =  AudioSystem.getLine(lineInfo);
					clip = (Clip) line;
					audioPlayback.setClip(clip);
				}
				catch(LineUnavailableException e) {
					System.out.println(e);
				}
			}
		}//End for
		
	}//End setMixerAndLine
	/**
	 * Prints out info about the mixers
	 */
	public void infoMixers() {
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (int i = 0; i < 8; i ++) {
			Mixer mixer = AudioSystem.getMixer(mixers[i]);
			System.out.println(mixer.getMixerInfo());
			for (int source = 0; source < mixer.getSourceLineInfo().length; source++) {
				System.out.println(mixer.getSourceLineInfo()[source]);
			}
			for (int target = 0; target < mixer.getTargetLineInfo().length; target++) {
				System.out.println(mixer.getTargetLineInfo()[target]);
			}
			System.out.println("--------------");
			System.out.println();
		}
	}//End infoMixers
	public Clip getClip() {
		return clip;
	}//End getClip
}
