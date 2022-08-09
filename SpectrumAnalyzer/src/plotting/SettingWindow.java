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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SettingWindow extends JFrame {

	private static final long serialVersionUID = 5872177811395185412L;
	private JPanel comp;
	private Plot parent;
	
	private int possiblePlots;
	private int selectedPlot = 1;
	private int[] selectedPlotS;
	
	


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
		//Adds the key bindings
		addKeyBinding(comp, KeyEvent.VK_ESCAPE, getName(), 0, (evt) -> {
			setVisible(false);
		});
	}//End SettingWindow
	
	/**
	 * Initializes the contentPane
	 */
	public void setContentPane() {
		setResizable(false);
		comp = new JPanel();
		comp.setBorder(new EmptyBorder(5, 5, 5, 5));
		comp.setBackground(new Color(80, 85, 110));
		setContentPane(comp);
		SpringLayout sl_contentPane = new SpringLayout();
		comp.setLayout(sl_contentPane);
		
		JLabel lbl_channels = new JLabel("Select channels: ");
		lbl_channels.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lbl_channels.setForeground(new Color(200, 200, 200));
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_channels, 5, SpringLayout.NORTH, comp);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_channels, 10, SpringLayout.WEST, comp);
		comp.add(lbl_channels);
		
		String[] channels = new String[possiblePlots];
		for (int i = 1; i <= possiblePlots; i++) {
			 channels[i - 1] = i + " ";
		}
		JList<String> list_channels = new JList<String>(channels);
		list_channels.setFont(new Font("Tahoma", Font.BOLD, 15));
		list_channels.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list_channels.setVisibleRowCount(1);
		list_channels.setBackground(new Color(80, 85, 110));
		list_channels.setForeground(Color.BLACK);
		list_channels.setSelectedIndex(0);
		list_channels.setFocusable(false);
		list_channels.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedPlotS = list_channels.getSelectedIndices();
				parent.setChannelToPlot(selectedPlotS);
			}
		});
		
		JScrollPane pane_channels = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pane_channels, 5, SpringLayout.SOUTH, lbl_channels);
		sl_contentPane.putConstraint(SpringLayout.WEST, pane_channels, 0, SpringLayout.WEST, lbl_channels);
		pane_channels.setBorder(null);
		pane_channels.setPreferredSize(new Dimension(lbl_channels.getPreferredSize().width, list_channels.getPreferredSize().height + 20));
		pane_channels.setViewportView(list_channels);
		comp.add(pane_channels);
		

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