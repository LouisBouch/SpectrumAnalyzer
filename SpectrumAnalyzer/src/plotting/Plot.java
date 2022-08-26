package plotting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import tools.Vector2D;

public class Plot extends JPanel {

	private static final long serialVersionUID = -8205056792145014780L;
	
	private Dimension panelSize;//Sets the panel size
	private JPanel panel = this;
	
	private SettingWindow waveFormSet;//The settings window
	
	private final double EPSILON = 1e-10;//Uncertainty value 
	
//	private Color[] colors = {Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK}; 
	private Color[] colors = {new Color(0x7dff6b), new Color(0xfaff6b), new Color(0xff6b6b), new Color(0x6b85ff), new Color(0xd96bff), new Color(0x6bd5ff), new Color(0xffab6b), new Color(0x8e8e8e)}; 
	private Color backGroundColor = new Color(5, 5, 5);

	private double[][] values;//The values of the plot for all different channels
	
	private int nbSamples;//Amount of samples in the channel
	private double samplesPerUnit;//The amount of samples required to make one unit (x axis)
	private int nbPossiblePlots;//The amount of plots that can be created from the different channels
	private int[] channelsToPlot = {0};//The channel to use to make the plot

	private double anticipationValue = 0.8;//Number between 0-1 with 0 giving a more compact grid and 1 giving a more spaced grid
	private double gridSize = 0.5;//Bigger values give bigger grids

	private int startingPixelsPerUnit = 100;//The starting amount of pixels per unit
	
	private double samplesPerPixel;//Amount of samples per pixel

	private double zoomPercentage = 0.9;//Zooming out reduces the size of objects by this amount
	private Vector2D mousePosVec = new Vector2D();//The x/y offset of the origin
	private Vector2D offsetVec;//The x/y offset of the origin
	private Vector2D scaleVec = new Vector2D(1, 1);//The separation of each unit on an axis
	private Vector2D pixelsPerUnitVec = new Vector2D(startingPixelsPerUnit, startingPixelsPerUnit);//The scale of the plot in the x/y direction. 1:1 -> 1 pixel per unit
	private Vector2D zoomAmountVec = new Vector2D();//Amount of times the window has been zoomed in or out (- -> in ; + -> out)
	private Vector2D divAmountByTwoVec = new Vector2D();//Amount of times the axes were split by 2
	private Vector2D divAmountByFiveHalfVec = new Vector2D();//Amount of times the axes were split by 5/2
	private Vector2D zoomInThresholdVec = new Vector2D();//When the screen is more zoomed in than this number, adjusts the scale
	private Vector2D zoomOutThresholdVec = new Vector2D();//When the screen is more zoomed in than this number, adjusts the scale
	private Vector2D pixelsPerStepVec = new Vector2D();//Amount of pixels in-between each step (white lines)


	private JLabel lbl_pixPerUX;
	private JLabel lbl_pixPerUY;
	
	private double[][][] minMaxArray;//Stores the max and minimum values of the current array
	
	private boolean arrayFillingNecessary = true;//Check if there is a need to refill the array
	
	private int sampleDensityThreshold = 800;//How many samples per pixels are required to switch to a different way of computing values
	

	/**
	 * Creates the object and the wave at the same time
	 * @param values The data from the wav file
	 * @param samplesPerUnit The amount of samples required to make one unit (x axis)
	 * @param plotsLegend A legend of each plot. Each array item contains information about the name of the plot
	 */
	public Plot(double[][] values, double samplesPerUnit, String[] plotsLegend) {
		prepPlot();
		loadWave(values, samplesPerUnit, plotsLegend);
	}
	/**
	 * Creates an empty plot
	 */
	public Plot() {
		prepPlot();
	}//End Plot

	/**
	 * Does the necessary to prepare the plot
	 */
	public void prepPlot() {
		setBackground(backGroundColor);
		panelSize = getPreferredSize();
		offsetVec = new Vector2D(panelSize.getWidth() * 1/2, panelSize.getHeight() * 1/2);

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		JButton btn_settings = new JButton("settings");
		btn_settings.setFocusable(false);
		springLayout.putConstraint(SpringLayout.NORTH, btn_settings, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btn_settings, -10, SpringLayout.EAST, this);
		add(btn_settings);
		
		lbl_pixPerUX = new JLabel("Pixels per unit, X axis: " + pixelsPerUnitVec.getX());
		lbl_pixPerUX.setForeground(Color.LIGHT_GRAY);
		springLayout.putConstraint(SpringLayout.NORTH, lbl_pixPerUX, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lbl_pixPerUX, 20, SpringLayout.WEST, this);
		add(lbl_pixPerUX);
		
		lbl_pixPerUY = new JLabel("Pixels per unit, Y axis: " + pixelsPerUnitVec.getY());
		lbl_pixPerUY.setForeground(Color.LIGHT_GRAY);
		springLayout.putConstraint(SpringLayout.NORTH, lbl_pixPerUY, 25, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lbl_pixPerUY, 20, SpringLayout.WEST, this);
		add(lbl_pixPerUY);
		
		//Handles the offset when the mouse is dragged in the frame
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				offsetVec.add(e.getX() - mousePosVec.getX(), e.getY() - mousePosVec.getY());
				
				mousePosVec.setValues(e.getX(), e.getY());

				repaint();
			}
		});//End addMouseMotionListener
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePosVec.setValues(e.getX(), e.getY());
				repaint();
			}
		});//End addMouseListener
		
		//Handles zooming in the frame
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				rescale(e);
				repaint();
			}
		});//End addMouseWheelListener
		
		btn_settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (waveFormSet != null) waveFormSet.activate();
				else JOptionPane.showMessageDialog(panel, "A file must be opened first");
			}
		});//End addActionListener
	}//End prepPlot
	
	/**
	 * Uses the information from the wav file to create the waveform
	 */
	public void loadWave(double[][] values, double samplesPerUnit, String[] plotsLegend) {
		this.values = values;
		this.samplesPerUnit = samplesPerUnit;
		nbPossiblePlots = values.length;
		nbSamples = 0;
		for (int plot = 0; plot < nbPossiblePlots; plot++) {//Checks for max samples
			nbSamples = values[plot].length > nbSamples ? values[plot].length : nbSamples;
		}
		//Takes care of the old plot
		if (waveFormSet != null) waveFormSet.close();
		waveFormSet = new SettingWindow(plotsLegend, this);
		arrayFillingNecessary = true;
		repaint();
	}//End loadWave
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		paintAxes(g2d);
		if (values != null) paintWaveForm(g2d);
	}//End paintComponent
	
	/**
	 * Paints the axes
	 * @param g2d The graphics item
	 */
	public void paintAxes(Graphics2D g2d) {
		Graphics2D g2dSoftLines = (Graphics2D) g2d.create();
		Graphics2D g2dText = (Graphics2D) g2d.create();

		g2dSoftLines.setColor(new Color(255, 255, 255, 75));
		g2dText.setColor(new Color(255, 255, 255, 175));

		//Draws main axes
		g2dText.drawLine(offsetVec.getFlooredX(), 0, offsetVec.getFlooredX(), getHeight());//y axis
		g2dText.drawLine(0, offsetVec.getFlooredY(), getWidth(), offsetVec.getFlooredY());//x axis

		g2dText.setColor(new Color(255, 255, 255, 255));

		
		double pixMCTB;//Represents the pixel that is a multiple of "pixelsPerStep" which is the closest to the border
		int xPos;
		int yPos;
		int i;//Index value
		pixelsPerStepVec.setValues(pixelsPerUnitVec.getX() * scaleVec.getX(), 
				pixelsPerUnitVec.getY() * scaleVec.getY()).multiply(gridSize);
		//Draws the unit increments on the x axis--------------------------------------------------------------------------------------
		pixMCTB = ((offsetVec.getX() % pixelsPerStepVec.getX()) + pixelsPerStepVec.getX()) % pixelsPerStepVec.getX();//Pixel multiple closest to border
		i = 0;
		do {
			xPos = (int) Math.round(pixMCTB + pixelsPerStepVec.getX()*i);
			yPos = offsetVec.getFlooredY();
			printXCenteredString((Math.round((-offsetVec.getX() + pixMCTB + pixelsPerStepVec.getX()*i)/pixelsPerUnitVec.getX()*10000)/10000.0) + "", xPos, yPos, g2dText);
			g2dSoftLines.drawLine(xPos, 0, xPos, getHeight());
			i++;
		}	while(pixelsPerStepVec.getX()*i < getWidth());

		//Draws the unit increments on the y axis--------------------------------------------------------------------------------------
		pixMCTB = ((offsetVec.getY() % pixelsPerStepVec.getY()) + pixelsPerStepVec.getY()) % pixelsPerStepVec.getY();
		i = 0;
		do {
			if (offsetVec.getY() -pixMCTB - pixelsPerStepVec.getY()*i < EPSILON && offsetVec.getY() -pixMCTB - pixelsPerStepVec.getY()*i > -EPSILON) {//Doesn't redraw the main axis and the other 0
				i++;
				continue;
			}
			xPos = offsetVec.getFlooredX();
			yPos = offsetVec.getFlooredY() + (int) Math.round(-offsetVec.getFlooredY() + pixMCTB + pixelsPerStepVec.getY()*i);
			printYCenteredString((Math.round((offsetVec.getFlooredY() -pixMCTB - pixelsPerStepVec.getY()*i)/pixelsPerUnitVec.getY()*10000)/10000.0) + "", xPos, yPos, g2dText);
			g2dSoftLines.drawLine(0, yPos, getWidth(), yPos);
			i++;
		}	while(pixelsPerStepVec.getY()*i < getHeight());
	}//End paintAxes
	
	/**
	 * Paints the waveform
	 * @param g2d The graphics item
	 */
	public void paintWaveForm(Graphics2D g2d) {
		samplesPerPixel = samplesPerUnit / pixelsPerUnitVec.getX();
		
		int sampleOffset = 0;//The amount of samples that are off screen due the the xOffset
		int remainingSamples = nbSamples;
		if (offsetVec.getX() < 0) {
			sampleOffset = (int) Math.round(samplesPerPixel * -offsetVec.getX());
			remainingSamples = nbSamples - sampleOffset;
		}
		//Only draws if some part of the waveform is visible
		if (remainingSamples > 0 && offsetVec.getX() < getWidth() && channelsToPlot.length != 0) {
			double pixelIncrement = samplesPerPixel >= 1 ? 1 : 1/samplesPerPixel;//Increments the x axis by this amount for each iteration of the next loop
			//Fills the full array if sample density is high enough
			if (arrayFillingNecessary && pixelIncrement == 1 && samplesPerPixel > sampleDensityThreshold) {
				minMaxArray = new double[channelsToPlot.length][(int) Math.ceil(nbSamples / samplesPerPixel)][2];
				fillArray(minMaxArray, values, channelsToPlot.length, channelsToPlot);
			}
			//Plots every selected channels
			for (int channel = 0; channel < channelsToPlot.length; channel++) {
				g2d.setColor(colors[channelsToPlot[channel] % colors.length]);//Channels keep their color

				int xIni;
				int yIni;
				int xFin;
				int yFin;

				int sampleNb;
				if (nbSamples > 1) {
					//More than 1 sample per pixel allows to increment x by 1 each time
					if (pixelIncrement == 1) {
						xIni = offsetVec.getX() <= 0 ? 0 : offsetVec.getFlooredX();
//						yIni = offsetVec.getFlooredY() - (int) Math.round(values[channelsToPlot[channel]][sampleOffset] * pixelsPerUnitVec.getY());
						
						int index = 0;
						int indexOffset = offsetVec.getX() < 0 ? -offsetVec.getFlooredX() : 0;
						
						//Low density draw values
						double[] minMax;
						int sampleSize;
						int lastSampleChecked = -1;
						int gap;//Gap between old value and new value
						do {
							xFin = xIni + 1;

							sampleNb = (int) Math.round((xIni - offsetVec.getX()) * samplesPerPixel);

							//First way to compute
							if (samplesPerPixel > sampleDensityThreshold) {
								yIni = offsetVec.getFlooredY() - (int) Math.round(minMaxArray[channel][index + indexOffset][0] * pixelsPerUnitVec.getY());
								yFin = offsetVec.getFlooredY() - (int) Math.round(minMaxArray[channel][index + indexOffset][1] * pixelsPerUnitVec.getY());
							}
							//Second way to compute
							else {
								//Fixes the rounding issues and makes sure every sample is checked
								sampleSize = (int) Math.round(samplesPerPixel);
								gap = sampleNb - (lastSampleChecked + 1);
								if (gap != 0) {
									sampleNb -= gap;
									sampleSize += gap;
								}

								minMax = minMaxOfSampleChunk(values[channelsToPlot[channel]], sampleNb, sampleSize);
								lastSampleChecked = (sampleNb + sampleSize - 1);
								yIni = offsetVec.getFlooredY() - (int) Math.round(minMax[0] * pixelsPerUnitVec.getY());
								yFin = offsetVec.getFlooredY() - (int) Math.round(minMax[1] * pixelsPerUnitVec.getY());
							}
							//Draw line with slight tilt to give a shadowy effect
							g2d.drawLine(xIni, offsetVec.getFlooredY(), xFin, yFin);
							g2d.drawLine(xIni, offsetVec.getFlooredY(), xFin, yIni);
//							g2d.drawLine(xIni, offsetVec.getFlooredY(), xIni, yFin);
//							g2d.drawLine(xIni, offsetVec.getFlooredY(), xIni, yIni);


							xIni = xFin;
							yIni = yFin;
							index++;
						} while(xFin + 1 < getWidth() && sampleNb + (int) Math.round(samplesPerPixel) < nbSamples);
					}
					
					else {//Increment by more than one pixel each time. Increments the values by one each time
						int iterationNb = 1;

						sampleNb = (int) ((offsetVec.getX() < 0 ? -offsetVec.getFlooredX() : 0) * samplesPerPixel);
						yIni = offsetVec.getFlooredY() - (int) Math.round(values[channelsToPlot[channel]][sampleNb] * pixelsPerUnitVec.getY());

						double xInitialeValue = (sampleNb / samplesPerPixel) + offsetVec.getX();//Decides the initial x position based on the sample used
						xIni = (int) xInitialeValue;

						do {
							xFin = (int) Math.round(xInitialeValue + iterationNb * pixelIncrement);
							yFin = offsetVec.getFlooredY() - (int) Math.round(values[channelsToPlot[channel]][sampleNb + 1] * pixelsPerUnitVec.getY());

							g2d.drawLine(xIni, yIni, xFin, yFin);

							xIni = xFin;
							yIni = yFin;

							iterationNb++;
							sampleNb++;
						} while(xFin < getWidth() && sampleNb + 1 < nbSamples);
					}//End if
				}//End if
				else g2d.drawLine(offsetVec.getFlooredX(), offsetVec.getFlooredY(), offsetVec.getFlooredX(), offsetVec.getFlooredY() - (int) Math.round(values[channelsToPlot[channel]][0] * pixelsPerUnitVec.getY()));//Draws only the first sample
			}//End plotting
			arrayFillingNecessary = false;
		}
		
	}//End paintWaveForm
	/**
	 * Fills the value array
	 */
	public void fillArray(double[][][] arrayToFill, double[][] values, int channels, int[] channelsToPlot) {
		int pixel = 0;
		int sampleStart;
		int sampleSize;
		int lastSampleChecked = -1;
		int gap;//Gap between old value and new value
		for (int channel = 0; channel < channels; channel++) {
			for (pixel = 0; pixel < arrayToFill[channel].length; pixel++) {
				sampleStart = (int) Math.round(pixel * samplesPerPixel);
				sampleSize = (int) Math.round(samplesPerPixel);
				gap = sampleStart - (lastSampleChecked + 1);
				//Fixes the rounding issues and makes sure every sample is checked
				if (gap != 0) {
					sampleStart -= gap;
					sampleSize += gap;
				}
				arrayToFill[channel][pixel] = minMaxOfSampleChunk(values[channelsToPlot[channel]], sampleStart, sampleSize);
				lastSampleChecked = (sampleStart + sampleSize - 1);
			}
		}
	}
	
	/**
	 * Gets the average of a chunk of samples
	 * @param values The array
	 * @param startingPoint The starting point in the array
	 * @param nbSamples The number of samples to go through
	 * @return The mean value
	 */
	public double meanValueOfSampleChunk(double[] values, int startingPoint, int nbSamples) {
		double value = 0;
		for (int index = 0; index < nbSamples; index++) {
			if (startingPoint + index >= values.length) {//Breaks if it goes beyond the max amount of samples
				nbSamples = index;
				break;
			}
			value += values[startingPoint + index];
		}
		return value/nbSamples;
	}//End meanValueOfSampleChunk
	
	/**
	 * Gets the minimum and maximum value of the sample
	 * @param values The array
	 * @param startingPoint The starting point in the array
	 * @param nbSamples The number of samples to go through
	 * @return The minimum[0] and maximum[1] value of the sample
	 */
	public double[] minMaxOfSampleChunk(double[] values, int startingPoint, int nbSamples) {
		double[] minMax = new double[2];
		double value;
		minMax[0] = values[startingPoint];
		minMax[1] = minMax[0];
		for (int index = 1; index < nbSamples; index++) {
			if (startingPoint + index >= values.length) {//Breaks if it goes beyond the max amount of samples
				break;
			}
			value = values[startingPoint + index];
			if (value < minMax[0]) minMax[0] = value;
			if (value > minMax[1]) minMax[1] = value;
		}
		return minMax;
	}//End minMaxOfSampleChunk
	
	
	/**
	 * Prints a centered string in the x direction (top aligned)
	 * @param text The text to print
	 * @param xPos The x coordinate of the center of the text
	 * @param yPos The y coordinate of the center of the text
	 * @param g2d The graphics tool
	 */
	public void printXCenteredString(String text, int xPos, int yPos, Graphics g2d) {
		Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		int strLength = (int) bounds.getWidth();
		int strHeight = (int) bounds.getHeight();
		
		//Makes sure the text doesn't go out of bounds
		if (yPos + strHeight > getHeight()) yPos = getHeight() - strHeight;
		if (yPos < 0) yPos = 0;
		
		g2d.drawString(text, xPos - strLength/2, yPos + strHeight);
	}
	/**
	 * Prints a centered string in the y direction (right aligned)
	 * @param text The text to print
	 * @param xPos The x coordinate of the center of the text
	 * @param yPos The y coordinate of the center of the text
	 * @param g2d The graphics tool
	 */
	public void printYCenteredString(String text, int xPos, int yPos, Graphics g2d) {
		Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
		int strLength = (int) bounds.getWidth();
		int strHeight = (int) bounds.getHeight();
		
		//Makes sure the text doesn't go out of bounds
		if (xPos - 4 > getWidth()) xPos = getWidth() + 4;
		if (xPos - strLength - 4 < 0) xPos = strLength + 4;
		
		
		g2d.drawString(text, xPos - strLength - 4, yPos + (int) (7/24.0 * strHeight));
	}
	/**
	 * Rescales the plot
	 */
	public void rescale(MouseWheelEvent e) {
		int modifier = e.getModifiersEx();//64 for Shift, 128 for Ctrl and 0 for nothing
		
		if (modifier == 64) zoomY(e);
		else if (modifier == 128) zoomX(e);
		else zoomAll(e);
		
	}
	/**
	 * Zooms all axes
	 */
	public void zoomAll(MouseWheelEvent e) {
		zoomX(e);
		zoomY(e);
		
	}
	/**
	 * Zooms only the x axis
	 */
	public void zoomX(MouseWheelEvent e) {
		arrayFillingNecessary = true;
		int zoomDirection = e.getWheelRotation();//(- -> in ; + -> out)
		zoomAmountVec.addX(zoomDirection);
		adjustThresholdsTight("x");
		
		//Adjusts the x/y offset to keep the cursor at the same coordinates when zooming in/out
		offsetVec.addX(Math.round((e.getX() - offsetVec.getX()) * (1 - Math.pow(zoomPercentage, zoomDirection))));
		
		//Adjusts the scale if too zoomed in or out for the x axis
		if (Math.pow(zoomPercentage, zoomAmountVec.getX()) >= zoomInThresholdVec.getX()) {//Too zoomed in
			if (((divAmountByTwoVec.getX() + divAmountByFiveHalfVec.getX()) % 3 + 3) % 3 == 1) {
				divAmountByFiveHalfVec.addX(1);
			}
			else {
				divAmountByTwoVec.addX(1);
			}
		}
		else if (Math.pow(zoomPercentage, zoomAmountVec.getX()) < zoomOutThresholdVec.getX()) {//Too zoomed out
			if (((divAmountByTwoVec.getX() + divAmountByFiveHalfVec.getX()) % 3 + 3) % 3 == 2) {
				divAmountByFiveHalfVec.addX(-1);
			}
			else {
				divAmountByTwoVec.addX(-1);
			}
		}
		
		//Adjusts the scale
		scaleVec.setX(Math.pow(2, -divAmountByTwoVec.getX()) * Math.pow(2.5, -divAmountByFiveHalfVec.getX()));

		//Manages the amount of pixels per unit
		pixelsPerUnitVec.setX(startingPixelsPerUnit * Math.pow(zoomPercentage, zoomAmountVec.getX()));

		//Gives a bit of info on the panel
		lbl_pixPerUX.setText("Pixels per unit, X axis: " + Math.round((pixelsPerUnitVec.getX() * 1000)) / 1000.0);
		
	}
	/**
	 * Zooms only the y axis
	 */
	public void zoomY(MouseWheelEvent e) {
		int zoomDirection = e.getWheelRotation();//(- -> in ; + -> out)
		zoomAmountVec.addY(zoomDirection);
		adjustThresholdsTight("y");

		//Adjusts the x/y offset to keep the cursor at the same coordinates when zooming in/out
		offsetVec.addY(Math.round((e.getY() - offsetVec.getY()) * (1 - Math.pow(zoomPercentage, zoomDirection))));

		//Adjusts the scale if too zoomed in or out for the y axis
		if (Math.pow(zoomPercentage, zoomAmountVec.getY()) >= zoomInThresholdVec.getY()) {//Too zoomed in
			if (((divAmountByTwoVec.getY() + divAmountByFiveHalfVec.getY()) % 3 + 3) % 3 == 1) {
				divAmountByFiveHalfVec.addY(1);
			}
			else {
				divAmountByTwoVec.addY(1);
			}
		}
		else if (Math.pow(zoomPercentage, zoomAmountVec.getY()) < zoomOutThresholdVec.getY()) {//Too zoomed out
			if (((divAmountByTwoVec.getY() + divAmountByFiveHalfVec.getY()) % 3 + 3) % 3 == 2) {
				divAmountByFiveHalfVec.addY(-1);
			}
			else {
				divAmountByTwoVec.addY(-1);
			}
		}

		//Adjusts the scale
		scaleVec.setY(Math.pow(2, -divAmountByTwoVec.getY()) * Math.pow(2.5, -divAmountByFiveHalfVec.getY()));

		//Manages the amount of pixels per unit
		pixelsPerUnitVec.setY(startingPixelsPerUnit * Math.pow(zoomPercentage, zoomAmountVec.getY()));

		//Gives a bit of info on the panel
		lbl_pixPerUY.setText("Pixels per unit, Y axis: " + Math.round((pixelsPerUnitVec.getY() * 1000)) / 1000.0);
	}
	
	/**
	 * Adjusts the zoom limits after a scale change. Works by anticipating the next value used to multiply
	 * The tightness of these thresholds make zooming more frequent
	 */
	public void adjustThresholdsTight(String axis) {
		int divAmountByTwo;
		int divAmountByFiveHalf;
		double zoomOutThreshold;
		double zoomInThreshold;
		
		//Checks which axis to change
		if (axis.equals("x")) {
			divAmountByTwo = divAmountByTwoVec.getFlooredX();
			divAmountByFiveHalf = divAmountByFiveHalfVec.getFlooredX();
		}
		else {
			divAmountByTwo = divAmountByTwoVec.getFlooredY();
			divAmountByFiveHalf = divAmountByFiveHalfVec.getFlooredY();
		}
		
		//Computes the threshold
		if (((divAmountByTwo + divAmountByFiveHalf) % 3 + 3) % 3 == 2) {//Scale change multiplied by 2.5
			zoomOutThreshold = Math.pow(2.5, divAmountByFiveHalf - (1 - anticipationValue)) * Math.pow(2, divAmountByTwo);
			zoomInThreshold = Math.pow(2.5, divAmountByFiveHalf) * Math.pow(2, divAmountByTwo + anticipationValue);
		}
		else if (((divAmountByTwo + divAmountByFiveHalf) % 3 + 3) % 3 == 1){//Scale change multiplied by second 2
			zoomOutThreshold = Math.pow(2.5, divAmountByFiveHalf) * Math.pow(2, divAmountByTwo - (1 - anticipationValue));
			zoomInThreshold = Math.pow(2.5, divAmountByFiveHalf + anticipationValue) * Math.pow(2, divAmountByTwo);
		}
		else {//Scale change multiplied by first 2
			zoomOutThreshold = Math.pow(2.5, divAmountByFiveHalf) * Math.pow(2, divAmountByTwo - (1 - anticipationValue));
			zoomInThreshold = Math.pow(2.5, divAmountByFiveHalf) * Math.pow(2, divAmountByTwo + anticipationValue);
		}
		
		//Sets the threshold
		if (axis.equals("x")) {
			zoomOutThresholdVec.setX(zoomOutThreshold);
			zoomInThresholdVec.setX(zoomInThreshold);
		}
		else {
			zoomOutThresholdVec.setY(zoomOutThreshold);
			zoomInThresholdVec.setY(zoomInThreshold);
		}
	}

	/**
	 * Makes sure the offset is in the middle of the plot
	 */
	@Override
	public void setPreferredSize(Dimension dimension) {
		super.setPreferredSize(dimension);
		panelSize = getPreferredSize();
		offsetVec = new Vector2D(panelSize.getWidth() * 1/2, panelSize.getHeight() * 1/2);
	}
	/**
	 * Sets the channel to plot
	 * @param channelToPlot The channel index
	 */
	public void setChannelToPlot(int[] channelToPlot) {
		this.channelsToPlot = channelToPlot;
		arrayFillingNecessary = true;
		repaint();
	}
	/**
	 * Gets the amount of possible plots
	 * @return Possible plots
	 */
	public int getNbPossiblePlots() {
		return nbPossiblePlots;
	}
	/**
	 * Gets the color array that draws the plots
	 * @return The color array
	 */
	public Color[] getColors() {
		return colors;
	}
	public int getxOffset() {
		return offsetVec.getFlooredX();
	}
	public double getSamplesPerPixel() {
		return samplesPerPixel;
	}
	public double getxPixelsPerUnit() {
		return pixelsPerUnitVec.getX();
	}
}

/**
 * Adjusts the zoom limits after a scale change. Works by anticipating the next value used to multiply.
 * The looseness of these thresholds make zooming less frequent
 */
/*
 * private double gridTightness = 1.5;//Number between 0.5-1.5 with the lower numbers giving more tightness to the grid

private boolean tightGrid = true;
public void adjustThresholdsLoose() {
	if (((xDivAmountByTwo + xDivAmountByFiveHalf) % 3 + 3) % 3 == 2) {//Scale change multiplied by 2.5
		zoomOutThreshold = Math.pow(2.5, xDivAmountByFiveHalf - gridTightness) * Math.pow(2, xDivAmountByTwo);
		zoomInThreshold = Math.pow(2.5, xDivAmountByFiveHalf) * Math.pow(2, xDivAmountByTwo + gridTightness);
	}
	else if (((xDivAmountByTwo + xDivAmountByFiveHalf) % 3 + 3) % 3 == 1){//Scale change multiplied by second 2
		zoomOutThreshold = Math.pow(2.5, xDivAmountByFiveHalf) * Math.pow(2, xDivAmountByTwo - gridTightness);
		zoomInThreshold = Math.pow(2.5, xDivAmountByFiveHalf + gridTightness) * Math.pow(2, xDivAmountByTwo);
	}
	else {//Scale change multiplied by first 2
		zoomOutThreshold = Math.pow(2.5, xDivAmountByFiveHalf) * Math.pow(2, xDivAmountByTwo - gridTightness);
		zoomInThreshold = Math.pow(2.5, xDivAmountByFiveHalf) * Math.pow(2, xDivAmountByTwo + gridTightness);
	}
}
*/
