package jFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jPanels.MainWindow;

public class Window extends JFrame {

	
	private static final long serialVersionUID = -5931557487717016550L;
	private JPanel contentPane;
	static Window frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		
		
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
					System.out.println(e.getX() + ", " + e.getY());
			}
		});
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screenSize.width / 10, screenSize.height / 10, screenSize.width / 2, screenSize.height / 2);
//		setBounds(-8, -8, screenSize.width + 16, screenSize.height - 24);
		setExtendedState(MAXIMIZED_BOTH);
		
		
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(new Color(0, 0, 0));
		
		MainWindow analyzer = new MainWindow();
		contentPane.add(analyzer);
		
		setContentPane(contentPane);
	}

}
