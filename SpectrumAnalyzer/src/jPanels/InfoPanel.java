package jPanels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import tools.ScreenSizeTool;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = -3472014902506067567L;
	private JPanel panel = this;
	private int width = ScreenSizeTool.WIDTH * 1/4;
	private int height = ScreenSizeTool.HEIGHT * 1/2;
	
	private JLabel lbl_textInfo;
	private Color backGroundColor = new Color(50, 55, 80);
	
	public InfoPanel() {
		setBackground(backGroundColor);
		setPreferredSize(new Dimension(width, height));
		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JScrollPane scrollPane_info = new JScrollPane();
		scrollPane_info.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		scrollPane_info.setBackground(this.getBackground());
		scrollPane_info.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane_info.getHorizontalScrollBar().setUnitIncrement(10);
		
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_info, 70, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_info, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_info, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_info, -10, SpringLayout.EAST, this);
		
		scrollPane_info.setPreferredSize(new Dimension(200, 200));
		add(scrollPane_info);
		
		lbl_textInfo = new JLabel("");
		lbl_textInfo.setForeground(Color.LIGHT_GRAY);
		lbl_textInfo.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_textInfo.setVerticalAlignment(SwingConstants.TOP);
		lbl_textInfo.setBackground(this.getBackground());
		lbl_textInfo.setOpaque(true);
		scrollPane_info.setViewportView(lbl_textInfo);
		
		JLabel lbl_infoLabel = new JLabel("Wav file info");
		lbl_infoLabel.setForeground(Color.LIGHT_GRAY);
		lbl_infoLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		springLayout.putConstraint(SpringLayout.NORTH, lbl_infoLabel, 24, SpringLayout.NORTH, this);
		lbl_infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lbl_infoLabel);
		
		JCheckBox checkBox_extraInfo = new JCheckBox("Detailed info");
		springLayout.putConstraint(SpringLayout.SOUTH, checkBox_extraInfo, -5, SpringLayout.NORTH, scrollPane_info);
		springLayout.putConstraint(SpringLayout.WEST, checkBox_extraInfo, 10, SpringLayout.WEST, this);
		checkBox_extraInfo.setBackground(backGroundColor);
		checkBox_extraInfo.setForeground(Color.LIGHT_GRAY);
		checkBox_extraInfo.setFocusable(false);
		checkBox_extraInfo.setSelected(true);
		add(checkBox_extraInfo);
		
		
		
		//Centers the label when the window is resized
		scrollPane_info.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int distance = (panel.getWidth() - lbl_infoLabel.getPreferredSize().width) / 2;
				springLayout.putConstraint(SpringLayout.WEST, lbl_infoLabel, distance, SpringLayout.WEST, panel);
				springLayout.putConstraint(SpringLayout.EAST, lbl_infoLabel, - distance, SpringLayout.EAST, panel);
				panel.revalidate();
			}
		});	
	}
	/**
	 * Sets the text in the JLabel
	 * @param info The text to be placed inside the JLabel
	 */
	public void setText(String info) {
		lbl_textInfo.setText(info);
	}
}
