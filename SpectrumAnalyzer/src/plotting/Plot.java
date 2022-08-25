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

import soundProcessing.AudioPlayback;
import tools.ScreenSizeTool;
import tools.Vector2D;

public class Plot extends JPanel implements Runnable {

	private static final long serialVersionUID = -8205056792145014780L;
	
	private Dimension panelSize;//Sets the panel size
	private JPanel panel = this;
	
	private AudioPlayback audio;
	private AudioBar bar = new AudioBar(this);
	private double playBackSpeed = 1;//Speed at which the audio is played
	private boolean stopped = true;//True if data was stopped, not paused
	
	
	private boolean running = false;//True if audio is playing

	private SettingWindow waveFormSet;//The settings window
	
	private final double EPSILON = 1e-10;//Uncertainty value 
	
//	private Color[] colors = {Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK}; 
	private Color[] colors = {new Color(0x7dff6b), new Color(0xfaff6b), new Color(0xff6b6b), new Color(0x6b85ff), new Color(0xd96bff), new Color(0x6bd5ff), new Color(0xffab6b), new Color(0x8e8e8e)}; 
	private Color backGroundColor = new Color(5, 5, 5);

	private double[][] values;//The values of the plot for all different channels
	
	private int nbSamples;//Amount of samples in the channel
	private double samplesPerUnit;//The amount of samples required to make one unit (x axis)
	private int nbPossiblePlots;//The amount of plots that can be created from the different channels
	private int[] channelsToPlot;//The channel to use to make the plot


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

	private double anticipationValue = 0.8;//Number between 0-1 with 0 giving a more compact grid and 1 giving a more spaced grid
	private double gridSize = 0.5;//Bigger values give bigger grids

	private JLabel lbl_pixPerUX;
	private JLabel lbl_pixPerUY;
	
	private double[][][] minMaxArray;//Stores the max and minimum values of the current array
	
	private boolean arrayFillingNecessary = true;//Check if there is a need to refill the array
	
	private int sampleDensityThreshold = 800;//How many samples per pixels are required to switch to a different way of computing values
	

	/**
	 * Creates the object and the wave at the same time
	 * @param values The data from the wav file
	 * @param samplesPerUnit The amount of samples required to make one unit (x axis)
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
		panelSize = new Dimension(ScreenSizeTool.WIDTH * 3/4, ScreenSizeTool.HEIGHT * 1/2);
		setPreferredSize(panelSize);
		offsetVec = new Vector2D(panelSize.getWidth() * 1/2, panelSize.getHeight() * 1/2);
		channelsToPlot = new int[1];
		channelsToPlot[0] = 0;

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
		
		
		scaleAdjust();
		
		//Handles the offset when the mouse is dragged in the frame
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
//				xOffset += e.getX() - mousePosX;
//				yOffset += e.getY() - mousePosY;
				offsetVec.add(e.getX() - mousePosVec.getX(), e.getY() - mousePosVec.getY());

//				mousePosX = e.getX();
//				mousePosY = e.getY();
				mousePosVec.setValues(e.getX(), e.getY());

				repaint();
			}
		});//End addMouseMotionListener
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
//				mousePosX = e.getX();
//				mousePosY = e.getY();
				mousePosVec.setValues(e.getX(), e.getY());
				if (e.getModifiersEx() == 1088) offsetVec.addX(1);
				if (e.getModifiersEx() == 1152) offsetVec.addX(-1);
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
		if (bar != null) bar.paint(g2d);
	}//End paintComponent
	
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
			if (arrayFillingNecessary && pixelIncrement == 1 && samplesPerPixel > sampleDensityThreshold) {
				minMaxArray = new double[channelsToPlot.length][(int) Math.ceil(nbSamples / samplesPerPixel)][2];
				fillArray(minMaxArray, values, channelsToPlot.length, channelsToPlot);
			}
			for (int channel = 0; channel < channelsToPlot.length; channel++) {//Plots every selected channels
				g2d.setColor(colors[channelsToPlot[channel] % colors.length]);//Channels keep their color

				int xIni;
				int yIni;
				int xFin;
				int yFin;

				int sampleNb;
//				int sampleLength;
				if (nbSamples > 1) {
					if (pixelIncrement == 1) {//More than 1 sample per pixel allows to increment x by 1 each time
						xIni = offsetVec.getX() < 0 ? 0 : offsetVec.getFlooredX();
//						yIni = yOffset - (int) Math.round(values[channelsToPlot[channel]][(int) Math.round((xIni - xOffset) * samplesPerPixels)] * yPixelsPerUnit);
						yIni = offsetVec.getFlooredY() - (int) Math.round(values[channelsToPlot[channel]][sampleOffset] * pixelsPerUnitVec.getY());
						//Fills the value array
						
						int index = 0;
						int indexOffset = offsetVec.getX() < 0 ? -offsetVec.getFlooredX() : 0;
						do {
							xFin = xIni + 1;

							sampleNb = (int) Math.round((xIni - offsetVec.getX()) * samplesPerPixel);
//							sampleLength = (int) Math.round((xFin - xOffset) * samplesPerPixel) - sampleNb;

							//First way to compute
							if (samplesPerPixel > sampleDensityThreshold) {
								yIni = offsetVec.getFlooredY() - (int) Math.round(minMaxArray[channel][index + indexOffset][0] * pixelsPerUnitVec.getY());
								yFin = offsetVec.getFlooredY() - (int) Math.round(minMaxArray[channel][index + indexOffset][1] * pixelsPerUnitVec.getY());
							}
							//Second way to compute
							else {
								double[] minMax = minMaxOfSampleChunk(values[channelsToPlot[channel]], sampleNb, (int) Math.round(samplesPerPixel));
								yIni = offsetVec.getFlooredY() - (int) Math.round(minMax[0] * pixelsPerUnitVec.getY());
								yFin = offsetVec.getFlooredY() - (int) Math.round(minMax[1] * pixelsPerUnitVec.getY());
							}
							g2d.drawLine(xIni, offsetVec.getFlooredY(), xFin, yFin);
							g2d.drawLine(xIni, offsetVec.getFlooredY(), xFin, yIni);


							xIni = xFin;
							yIni = yFin;
							index++;
						} while(xFin + 1 < getWidth() && sampleNb + (int) 2*Math.round(samplesPerPixel) < nbSamples);
					}
					else {//Increment by more than one pixel each time. Increments the values by one each time
						int iterationNb = 1;

//						sampleNb = (int) (( (xOffset < 0 ? 0 : xOffset) - xOffset) * samplesPerPixels);
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
		for (int channel = 0; channel < channels; channel++) {
			for (int sample = 0; sample < arrayToFill[channel].length - 1; sample++) {
				arrayToFill[channel][sample] = minMaxOfSampleChunk(values[channelsToPlot[channel]], (int) Math.round(sample * samplesPerPixel), (int) Math.round(samplesPerPixel));
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
	 * Paints the axes
	 * @param g2d The graphics item
	 */
	public void paintAxes(Graphics2D g2d) {
		Graphics g2dSoftLines = g2d.create();
		Graphics g2dText = g2d.create();

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
		//Draws the unit increments on the x axis--------------------------------------------------------------------------------------
		double pixelsPerXStep = pixelsPerUnitVec.getX() * scaleVec.getX();//Amount of pixel in-between white lines
		pixMCTB = ((offsetVec.getX() % pixelsPerXStep) + pixelsPerXStep) % pixelsPerXStep;//Pixel multiple closest to border
		i = 0;
		do {
			xPos = (int) Math.round(pixMCTB + pixelsPerXStep*i);
			yPos = offsetVec.getFlooredY();
			printXCenteredString((Math.round((-offsetVec.getX() + pixMCTB + pixelsPerXStep*i)/pixelsPerUnitVec.getX()*10000)/10000.0) + "", xPos, yPos, g2dText);
			g2dSoftLines.drawLine(xPos, 0, xPos, getHeight());
			i++;
		}	while(pixelsPerXStep*i < getWidth());

		//Draws the unit increments on the y axis--------------------------------------------------------------------------------------
		double pixelsPerYStep = pixelsPerUnitVec.getY() * scaleVec.getY();//Amount of pixel in-between white lines
		pixMCTB = ((offsetVec.getY() % pixelsPerYStep) + pixelsPerYStep) % pixelsPerYStep;
		i = 0;
		do {
			if (offsetVec.getY() -pixMCTB - pixelsPerYStep*i < EPSILON && offsetVec.getY() -pixMCTB - pixelsPerYStep*i > -EPSILON) {//Doesn't redraw the main axis and the other 0
				i++;
				continue;
			}
			xPos = offsetVec.getFlooredX();
			yPos = offsetVec.getFlooredY() + (int) Math.round(-offsetVec.getFlooredY() + pixMCTB + pixelsPerYStep*i);
			printYCenteredString((Math.round((offsetVec.getFlooredY() -pixMCTB - pixelsPerYStep*i)/pixelsPerUnitVec.getY()*10000)/10000.0) + "", xPos, yPos, g2dText);
			g2dSoftLines.drawLine(0, yPos, getWidth(), yPos);
			i++;
		}	while(pixelsPerYStep*i < getHeight());
	}//End paintAxes
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
//		xZoomAmount += zoomDirection;
		zoomAmountVec.addX(zoomDirection);
		adjustThresholdsTight("x");
		
		//Adjusts the x/y offset to keep the cursor at the same coordinates when zooming in/out
//		xOffset += Math.round((e.getX() - xOffset) * (1 - Math.pow(zoomPercentage, zoomDirection)));
		offsetVec.addX(Math.round((e.getX() - offsetVec.getX()) * (1 - Math.pow(zoomPercentage, zoomDirection))));
		
		//Adjusts the scale if too zoomed in or out for the x axis
		if (Math.pow(zoomPercentage, zoomAmountVec.getX()) >= zoomInThresholdVec.getX()) {//Too zoomed in
			if (((divAmountByTwoVec.getX() + divAmountByFiveHalfVec.getX()) % 3 + 3) % 3 == 1) {
//				xDivAmountByFiveHalf++;
				divAmountByFiveHalfVec.addX(1);
			}
			else {
//				xDivAmountByTwo++;
				divAmountByTwoVec.addX(1);
			}
		}
		else if (Math.pow(zoomPercentage, zoomAmountVec.getX()) < zoomOutThresholdVec.getX()) {//Too zoomed out
			if (((divAmountByTwoVec.getX() + divAmountByFiveHalfVec.getX()) % 3 + 3) % 3 == 2) {
//				xDivAmountByFiveHalf--;
				divAmountByFiveHalfVec.addX(-1);
			}
			else {
//				xDivAmountByTwo--;
				divAmountByTwoVec.addX(-1);
			}
		}
		
		//Adjusts the scale
//		xScale = Math.pow(2, -divAmountByTwoVec.getX()) * Math.pow(2.5, -divAmountByFiveHalfVec.getX());
		scaleVec.setX(Math.pow(2, -divAmountByTwoVec.getX()) * Math.pow(2.5, -divAmountByFiveHalfVec.getX()));

		//Manages the amount of pixels per unit
//		xPixelsPerUnit = startingPixelsPerUnit * Math.pow(zoomPercentage, zoomAmountVec.getX());
		pixelsPerUnitVec.setX(startingPixelsPerUnit * Math.pow(zoomPercentage, zoomAmountVec.getX()));

		//Gives a bit of info on the panel
		lbl_pixPerUX.setText("Pixels per unit, X axis: " + Math.round((pixelsPerUnitVec.getX() * 1000)) / 1000.0);
		
	}
	/**
	 * Zooms only the y axis
	 */
	public void zoomY(MouseWheelEvent e) {
		int zoomDirection = e.getWheelRotation();//(- -> in ; + -> out)
//		yZoomAmount += zoomDirection;
		zoomAmountVec.addY(zoomDirection);
		adjustThresholdsTight("y");

		//Adjusts the x/y offset to keep the cursor at the same coordinates when zooming in/out
//		yOffset += Math.round((e.getY() - yOffset) * (1 - Math.pow(zoomPercentage, zoomDirection)));
		offsetVec.addY(Math.round((e.getY() - offsetVec.getY()) * (1 - Math.pow(zoomPercentage, zoomDirection))));

		//Adjusts the scale if too zoomed in or out for the y axis
		if (Math.pow(zoomPercentage, zoomAmountVec.getY()) >= zoomInThresholdVec.getY()) {//Too zoomed in
			if (((divAmountByTwoVec.getY() + divAmountByFiveHalfVec.getY()) % 3 + 3) % 3 == 1) {
//				yDivAmountByFiveHalf++;
				divAmountByFiveHalfVec.addY(1);
			}
			else {
//				yDivAmountByTwo++;
				divAmountByTwoVec.addY(1);
			}
		}
		else if (Math.pow(zoomPercentage, zoomAmountVec.getY()) < zoomOutThresholdVec.getY()) {//Too zoomed out
			if (((divAmountByTwoVec.getY() + divAmountByFiveHalfVec.getY()) % 3 + 3) % 3 == 2) {
//				yDivAmountByFiveHalf--;
				divAmountByFiveHalfVec.addY(-1);
			}
			else {
//				yDivAmountByTwo--;
				divAmountByTwoVec.addY(-1);
			}
		}

		//Adjusts the scale
//		yScale = Math.pow(2, -divAmountByTwoVec.getY()) * Math.pow(2.5, -divAmountByFiveHalfVec.getY());
		scaleVec.setY(Math.pow(2, -divAmountByTwoVec.getY()) * Math.pow(2.5, -divAmountByFiveHalfVec.getY()));

		//Manages the amount of pixels per unit
//		yPixelsPerUnit = startingPixelsPerUnit * Math.pow(zoomPercentage, zoomAmountVec.getY());
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
//			divAmountByTwo = xDivAmountByTwo;
			divAmountByTwo = divAmountByTwoVec.getFlooredX();
			divAmountByFiveHalf = divAmountByFiveHalfVec.getFlooredX();
		}
		else {
//			divAmountByTwo = yDivAmountByTwo;
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
//			xZoomOutThreshold = zoomOutThreshold * gridSize;
			zoomOutThresholdVec.setX(zoomOutThreshold * gridSize);
//			xZoomInThreshold = zoomInThreshold * gridSize;
			zoomInThresholdVec.setX(zoomInThreshold * gridSize);
		}
		else {
//			yZoomOutThreshold = zoomOutThreshold * gridSize;
			zoomOutThresholdVec.setY(zoomOutThreshold * gridSize);
//			yZoomInThreshold = zoomInThreshold * gridSize;
			zoomInThresholdVec.setY(zoomInThreshold * gridSize);
		}
	}

	/**
	 * Adjusts the scale depending on the gridsize value
	 */
	public void scaleAdjust() {
		boolean finished = false;
		//Adjusts the scale if too zoomed in or out for both axes
		do {
			adjustThresholdsTight("x");
			adjustThresholdsTight("y");
			//y
			if (Math.pow(zoomPercentage, zoomAmountVec.getY()) >= zoomInThresholdVec.getY()) {//Too zoomed in
				if (((divAmountByTwoVec.getY() + divAmountByFiveHalfVec.getY()) % 3 + 3) % 3 == 1) {
//					yDivAmountByFiveHalf++;
					divAmountByFiveHalfVec.addY(1);
				}
				else {
					divAmountByTwoVec.addY(1);
				}
			}
			else if (Math.pow(zoomPercentage, zoomAmountVec.getY()) < zoomOutThresholdVec.getY()) {//Too zoomed out
				if (((divAmountByTwoVec.getY() + divAmountByFiveHalfVec.getY()) % 3 + 3) % 3 == 2) {
//					yDivAmountByFiveHalf--;
					divAmountByFiveHalfVec.addY(-1);
				}
				else {
//					yDivAmountByTwo--;
					divAmountByTwoVec.addY(-1);
				}
			} else finished = true;
			//x
			if (Math.pow(zoomPercentage, zoomAmountVec.getX()) >= zoomInThresholdVec.getX()) {//Too zoomed in
				if (((divAmountByTwoVec.getX() + divAmountByFiveHalfVec.getX()) % 3 + 3) % 3 == 1) {
//					xDivAmountByFiveHalf++;
					divAmountByFiveHalfVec.addX(1);
				}
				else {
//					xDivAmountByTwo++;
					divAmountByTwoVec.addX(1);
				}
			}
			else if (Math.pow(zoomPercentage, zoomAmountVec.getX()) < zoomOutThresholdVec.getX()) {//Too zoomed out
				if (((divAmountByTwoVec.getX() + divAmountByFiveHalfVec.getX()) % 3 + 3) % 3 == 2) {
//					xDivAmountByFiveHalf--;
					divAmountByFiveHalfVec.addX(-1);
				}
				else {
//					xDivAmountByTwo--;
					divAmountByTwoVec.addX(-1);
				}
			} else finished = true;
		} while (!finished);

		//Adjusts the scale
//		yScale = Math.pow(2, -divAmountByTwoVec.getY()) * Math.pow(2.5, -divAmountByFiveHalfVec.getY());
//		xScale = Math.pow(2, -divAmountByTwoVec.getX()) * Math.pow(2.5, -divAmountByFiveHalfVec.getX());
		scaleVec.setValues(Math.pow(2, -divAmountByTwoVec.getX()) * Math.pow(2.5, -divAmountByFiveHalfVec.getX()),
				Math.pow(2, -divAmountByTwoVec.getX()) * Math.pow(2.5, -divAmountByFiveHalfVec.getX()));

		repaint();
	}
	/**
	 * Runs the audio bar
	 */
	@Override
	public void run() {
		double ini = System.nanoTime();
		
		//Makes sure everything starts at the same time
		while (audio.getClip().getMicrosecondPosition() == 0) {
			ini = System.nanoTime();
			sleep(5);
		}
		double offset = audio.getClip().getMicrosecondPosition()*playBackSpeed*1E-6 - (System.nanoTime()-ini)*playBackSpeed*1E-9;
		if (offset != 0) ini -= offset / playBackSpeed * 1E9;
		int rep = 0;
		
		//Adjusts the bar and repaints
		while(running) {
			rep = (rep+1) % 100;
			bar.setTimeOffset(1E-9*(System.nanoTime() - ini)*playBackSpeed);
			if (rep == 1) {
				offset = audio.getClip().getMicrosecondPosition()*playBackSpeed*1E-6 - (System.nanoTime()-ini)*playBackSpeed*1E-9;
				if (Math.abs(offset) > 0.05) {
					ini -= offset / playBackSpeed * 1E9;
				}
			}
			if((System.nanoTime()-ini)*playBackSpeed >= audio.getClip().getMicrosecondLength() * 1E3 * playBackSpeed) {
				stop();
				audio.stop();
			}
			repaint();
			sleep(10);
		}
		if (stopped) bar.setTimeOffset(0);
		repaint();
	}
	/**
	 * Pauses the threads
	 * @param sleep Amount of time to sleep in milliseconds
	 */
	public void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		}
		catch(InterruptedException e) {
			System.out.println(e);
		}
	}
	/**
	 * Starts the repaints
	 */
	public void start() {
		if (!running) {
			running = true;
			stopped = false;
			final Thread thread = new Thread(this);
			thread.start();
		}
	}
	/**
	 * Pauses the repaints
	 */
	public void pause() {
		if (running) running = false;
	}
	/**
	 * Stops the repaints
	 */
	public void stop() {
		if (running) running = false;
		bar.setTimeOffset(0);
		stopped = true;
		repaint();
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
//	/**
//	 * Gets the information about the wav file
//	 * @return The infoReservoir variable
//	 */
//	public WavInfo getInfoReservoir() {
//		return infoReservoir;
//	}
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
	public void setAudio(AudioPlayback audio) {
		this.audio = audio;
	}
	public double getSamplesPerPixel() {
		return samplesPerPixel;
	}
	public double getxPixelsPerUnit() {
		return pixelsPerUnitVec.getX();
	}
	public void setPlayBackSpeed(double playBackSpeed) {
		this.playBackSpeed = playBackSpeed;
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
