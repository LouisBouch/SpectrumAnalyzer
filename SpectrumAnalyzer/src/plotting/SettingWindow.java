package plotting;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

public class SettingWindow extends JFrame {

	private static final long serialVersionUID = 5872177811395185412L;
	private JPanel contentPane;
	private Plot parent;
	
	private int possiblePlots;
	private int selectedPlot = 1;


	public SettingWindow(int possiblePlots, Plot parent) {
		this.parent = parent;
		this.possiblePlots = possiblePlots;
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		
		setVisible(false);
		
		setContentPane();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}
	
	/**
	 * Initializes the contentPane
	 */
	public void setContentPane() {
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.DARK_GRAY);
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		String[] channels = new String[possiblePlots];
		for (int i = 1; i <= possiblePlots; i++) {
			 channels[i - 1] = i + "";
		}
		JLabel lbl_channels = new JLabel("Select a channel: ");
		lbl_channels.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lbl_channels.setForeground(new Color(200, 200, 200));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_channels, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_channels, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lbl_channels);
		
		JComboBox<String> comboBox = new JComboBox<String>(channels);
		sl_contentPane.putConstraint(SpringLayout.NORTH, comboBox, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, comboBox, 0, SpringLayout.EAST, lbl_channels);
		contentPane.add(comboBox);
		
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.setChannelToPlot(Integer.parseInt(comboBox.getSelectedItem().toString()));
			}
		});
		
		
	}
	/**
	 * Gets the channel to plot
	 * @return The channel to plot
	 */
	public int getSelectedPlot() {
		return selectedPlot;
	}
	
	/**
	 * Activates the settings window
	 */
	public void activate() {
		setVisible(true);
	}

	
}
