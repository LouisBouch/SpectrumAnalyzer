package plotting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import subchunksAndInfo.Chunk_fmt;

public class SettingWindow extends JFrame {

	private static final long serialVersionUID = 5872177811395185412L;
	private JPanel pane;
	private Plot parent;
	
	private int possiblePlots;
	private String[] detailedPossiblePlots;
	private int selectedPlot = 1;
	private int[] selectedPlots;
	
	private int channelFontSize = 15;
	

	public SettingWindow(Plot parent) {
		this.parent = parent;
		this.possiblePlots = parent.getNbPossiblePlots();
		
		//Sets detailedPossiblePlots
		String singleStringDetailedPossiblePlots = ((Chunk_fmt) parent.getWavInfo().getSubChunks().get("subchunksAndInfo.Chunk_fmt")).getChannelsLocationLongName();
		detailedPossiblePlots = new String[possiblePlots];
		for (int channel = 0; channel < possiblePlots; channel++) {
			if (singleStringDetailedPossiblePlots == "") break;
			detailedPossiblePlots[channel] = singleStringDetailedPossiblePlots.substring(0, singleStringDetailedPossiblePlots.indexOf('.'));
			singleStringDetailedPossiblePlots = singleStringDetailedPossiblePlots.substring(singleStringDetailedPossiblePlots.indexOf('.') + 1, singleStringDetailedPossiblePlots.length());
		}
		detailedPossiblePlots[possiblePlots - 1] += " ";
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		setVisible(false);
		
		setContentPane();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		//Adds the key bindings
		addKeyBinding(pane, KeyEvent.VK_ESCAPE, getName(), 0, (evt) -> {
			setVisible(false);
		});
	}//End SettingWindow
	
	/**
	 * Initializes the contentPane
	 */
	public void setContentPane() {
		setResizable(false);
		pane = new JPanel();
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane.setBackground(new Color(80, 85, 110));
		setContentPane(pane);
		SpringLayout sl_contentPane = new SpringLayout();
		pane.setLayout(sl_contentPane);
		
		JLabel lbl_channels = new JLabel("Select channels: ");
		lbl_channels.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lbl_channels.setForeground(new Color(200, 200, 200));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_channels, 5, SpringLayout.NORTH, pane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_channels, 10, SpringLayout.WEST, pane);
		pane.add(lbl_channels);
		
		String[] channels = new String[possiblePlots];
		channelFontSize = 15;
		for (int i = 1; i <= possiblePlots; i++) {
			channels[i - 1] = i + " ";
		}
		JList<String> list_channels = new JList<String>(channels);
		list_channels.setFont(new Font("Tahoma", Font.BOLD, channelFontSize));
		list_channels.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list_channels.setVisibleRowCount(1);
		list_channels.setBackground(pane.getBackground());
		list_channels.setForeground(Color.BLACK);
		list_channels.setSelectedIndex(0);
		list_channels.setFocusable(false);
		list_channels.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedPlots = list_channels.getSelectedIndices();
				parent.setChannelToPlot(selectedPlots);
			}
		});
		
		JScrollPane scrollPane_channels = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_channels, 5, SpringLayout.SOUTH, lbl_channels);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_channels, 0, SpringLayout.WEST, lbl_channels);
		scrollPane_channels.setBorder(null);
		scrollPane_channels.setPreferredSize(new Dimension(lbl_channels.getPreferredSize().width, list_channels.getPreferredSize().height + 20));
		scrollPane_channels.setViewportView(list_channels);
		pane.add(scrollPane_channels);

		JLabel label_legend = new JLabel("<html> <B>Legend:</B>");
		Color color;
		String red;
		String green;
		String blue;
		for (int channel = 1; channel <= possiblePlots; channel++) {
			color = parent.getColors()[channel - 1];
			red = Integer.toHexString(color.getRed());
			green = Integer.toHexString(color.getGreen());
			blue = Integer.toHexString(color.getBlue());
			String hexColorFont = "<font color=#" + (red.length() == 1 ? (red = "0" + red) : red) + (green.length() == 1 ? (green = "0" + green) : green) + (blue.length() == 1 ? (blue = "0" + blue) : blue) + ">";
			label_legend.setText(label_legend.getText() + "<br/>Channel " + hexColorFont + channel + "</font>: " + detailedPossiblePlots[channel - 1]);
		}
		label_legend.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label_legend.setForeground(Color.BLACK);
		label_legend.setBackground(pane.getBackground());
		label_legend.setText(label_legend.getText() + "<html>");
		label_legend.setVerticalAlignment(SwingConstants.TOP);

		UIManager.put("ScrollBar.width", 15);
		JScrollPane scrollPane_channelsLegend = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane_channelsLegend, 5, SpringLayout.SOUTH, scrollPane_channels);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane_channelsLegend, 0, SpringLayout.WEST, scrollPane_channels);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane_channelsLegend, 0, SpringLayout.EAST, scrollPane_channels);
		scrollPane_channelsLegend.setOpaque(false);
		scrollPane_channelsLegend.getViewport().setOpaque(false);
		scrollPane_channelsLegend.setBorder(new LineBorder(new Color(70, 75, 100)));

		int scrollBarAdjustement = 5;
		if (label_legend.getPreferredSize().getWidth() > lbl_channels.getPreferredSize().width) scrollBarAdjustement += 12;//Takes into account the vertical scroll bar that will get in the way
		int scrollPaneHeight = (getHeight() * 11/20 - scrollBarAdjustement < label_legend.getPreferredSize().getHeight()) ? (getHeight() * 11/20) : (int) label_legend.getPreferredSize().getHeight() + scrollBarAdjustement;

		scrollPane_channelsLegend.setPreferredSize(new Dimension(lbl_channels.getPreferredSize().width, scrollPaneHeight));
		scrollPane_channelsLegend.setViewportView(label_legend);
		pane.add(scrollPane_channelsLegend);

		

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
	
	/**
	 * Closes the window
	 */
	public void close() {
		setVisible(false);
	}
	
	/**
	 * Adds a key binding to a JComponent
	 * @param comp The JComponent to add the binding to
	 * @param keycode The key which receives the key binding
	 * @param id The name of the action
	 * @param modifier The key modifier
	 * @param actionListener The action to perform (Preferable to use a lambda exp)
	 */
	public void addKeyBinding(JComponent comp, int keycode, String id, int modifier, ActionListener actionListener) {
		InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap ap = comp.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(keycode, modifier, false), id);
		ap.put(id, new AbstractAction() {
			private static final long serialVersionUID = -4386169709521420145L;

			@Override
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(e);
			}
		});
	}//End addKeyBinding
	
}