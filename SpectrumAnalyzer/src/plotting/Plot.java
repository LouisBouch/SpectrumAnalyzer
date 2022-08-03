package plotting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import subchunksAndInfo.WavInfo;
import tools.ScreenSize;

public class Plot extends JPanel {

	private static final long serialVersionUID = -8205056792145014780L;

	private WavInfo wavInfo;//Contains the information about the wav file
	
	private final double EPSILON = 1e-10;//Uncertainty value 

	private double[][] values;//The values of the plot for all different channels
	private int nbSamples;//Amount of samples in the channel
	private double samplesPerUnit;//The amount of samples required to make one unit (x axis)
	private int nbPossiblePlots;//The amount of plots that can be created from the different channels
	private int channelToPlot = 0;//The channel to use to make the plot


	private int xOffset;//The x offset of the origin
	private int yOffset; //The y offset of the origin

	private double xScale = 1;//The separation of each unit on the x axis
	private double yScale = 1;//The separation of each unit on the y axis
	private int startingPixelsPerUnit = 100;//The starting amount of pixels per unit
	private double xPixelsPerUnit = startingPixelsPerUnit;//The scale of the plot in the x direction. 1:1 -> 1 pixel per unit
	private double yPixelsPerUnit = startingPixelsPerUnit;//The scale of the plot in the y direction. 1:1 -> 1 pixel per unit

	private int mousePosX;
	private int mousePosY;

	private int i;//Iterative index

	private double zoomPercentage = 0.9;//Zooming out reduces the size of objects by this amount
	private int xZoomAmount;//Amount of times the window has been zoomed in or out (- -> in ; + -> out) (x axis)
	private int yZoomAmount;//Amount of times the window has been zoomed in or out (- -> in ; + -> out) (y axis)
	private int xDivAmountByTwo = 0;//Amount of times the axes were split by 2 (x axis)
	private int yDivAmountByTwo = 0;//Amount of times the axes were split by 2 (y axis)
	private int xDivAmountByFiveHalf = 0;//Amount of times the axes were split by 5/2 (x axis)
	private int yDivAmountByFiveHalf = 0;//Amount of times the axes were split by 5/2 (y axis)
	private double xZoomInThreshold;//When the screen is more zoomed in than this number, adjusts the scale
	private double yZoomInThreshold;//When the screen is more zoomed in than this number, adjusts the scale
	private double xZoomOutThreshold;//When the screen is more zoomed out than this number, adjusts the scale
	private double yZoomOutThreshold;//When the screen is more zoomed out than this number, adjusts the scale

	private double anticipationValue = 0.5;//Number between 0-1 with 0 giving a more compact grid and 1 giving a more spaced grid
	private double gridSize = 1;//Bigger values give bigger grids
	private double gridTightness = 1.5;//Number between 0.5-1.5 with the lower numbers giving more tightness to the grid

	private boolean tightGrid = true;

	private JLabel lbl_pixPerUX;
	private JLabel lbl_pixPerUY;


	/**
	 * Creates the object and the wave at the same time
	 * @param values The data from the wav file
	 * @param samplesPerUnit The amount of samples required to make one unit (x axis)
	 */
	public Plot(double[][] values, double samplesPerUnit) {
		this.values = values;
		this.samplesPerUnit = samplesPerUnit;
		nbPossiblePlots = this.values.length;
		prepPlot();
		loadWave();
	}
	/**
	 * Creates an empty plot
	 */
	public Plot() {
		prepPlot();
	}

	/**
	 * Does the necessary to prepare the plot
	 */
	public void prepPlot() {
		setBackground(new Color(0, 0, 0));
		setPreferredSize(new Dimension(ScreenSize.width * 3/4, ScreenSize.height * 1/2));
		xOffset = ScreenSize.width * 3/4 * 1/2;
		yOffset = ScreenSize.height * 1/2 * 1/2;

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		JButton btn_settings = new JButton("settings");
		btn_settings.setFocusable(false);
		springLayout.putConstraint(SpringLayout.NORTH, btn_settings, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btn_settings, -10, SpringLayout.EAST, this);
		add(btn_settings);
		
		lbl_pixPerUX = new JLabel("Pixels per unit, X axis: " + xPixelsPerUnit);
		lbl_pixPerUX.setForeground(Color.LIGHT_GRAY);
		springLayout.putConstraint(SpringLayout.NORTH, lbl_pixPerUX, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lbl_pixPerUX, 20, SpringLayout.WEST, this);
		add(lbl_pixPerUX);
		
		lbl_pixPerUY = new JLabel("Pixels per unit, Y axis: " + yPixelsPerUnit);
		lbl_pixPerUY.setForeground(Color.LIGHT_GRAY);
		springLayout.putConstraint(SpringLayout.NORTH, lbl_pixPerUY, 25, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lbl_pixPerUY, 20, SpringLayout.WEST, this);
		add(lbl_pixPerUY);

		//Handles the offset when the mouse is dragged in the frame
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				xOffset += e.getX() - mousePosX;
				yOffset += e.getY() - mousePosY;

				mousePosX = e.getX();
				mousePosY = e.getY();

				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePosX = e.getX();
				mousePosY = e.getY();
			}
		});
		//Handles zooming in the frame
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				rescale(e);
				repaint();
			}
		});

	}
	/**
	 * Uses the information from the wav file to create the waveform
	 */
	public void loadWave() {
		if (wavInfo != null) {
			values = wavInfo.getChannelSeparatedData();
			if (values != null) {
				samplesPerUnit = wavInfo.getSampleRate();
				nbPossiblePlots = values.length;
				nbSamples = values[channelToPlot].length;
				repaint();
			}
		}
		for (int i = 0; i < values[0].length; i++) {
			values[0][i] = (i % 100) / 100000.0;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.RED);

		paintAxes(g2d);
		if (values != null) paintWaveForm(g2d);

	}
	/**
	 * Paints the waveform
	 * @param g2d The graphics item
	 */
	public void paintWaveForm(Graphics2D g2d) {
		/* REDO WITH NEW ZOOM FUNCTIONALITIES
		 * 
		boolean waveFormOutOfBounds = false;
		double samplesPerPixel = samplesPerUnit / xPixelsPerUnit;//Amount of samples that fit into a pixel

		//Computes the amount of samples out of bounds from the left
		double sampleWaveFormOffset = 0;
		if (xOffset < 0) {
			sampleWaveFormOffset = -xOffset * samplesPerPixel;
			if (sampleWaveFormOffset > nbSamples) waveFormOutOfBounds = true;
//			System.out.println("Offset: " + xOffset + " samples left: " + (nbSamples - sampleWaveFormOffset) + " samplesPerPixel: " + samplesPerPixel + " pixWidth: " + waveFormWidthPix + " pixPerUnit: " + xPixelsPerUnit);
		}
		//Draws the waveform
		if (!waveFormOutOfBounds) {
			int xStart = 0;
			int yStart = (int) values[channelToPlot][(int) sampleWaveFormOffset];
			int xEnd;
			int yEnd;

			for (i = 0; i < ((nbSamples - sampleWaveFormOffset) / samplesPerPixel) - 1; i += 1) {//Note: Doesn't draw the last pixel
				xEnd = xStart + 1;
				yEnd = (int) (meanValueOfSampleChunk(values[channelToPlot], (int) sampleWaveFormOffset + (int) (samplesPerPixel)*i, (int) samplesPerPixel) * yPixelsPerUnit);
//				yEnd = (int) (values[channelToPlot][(int) sampleWaveFormOffset + (int) (samplesPerPixel)*i] * yPixelsPerUnit);
				int additionalXOffset = (int) (sampleWaveFormOffset / samplesPerPixel);//The additional sample offset
//				System.out.println("max samples: " + nbSamples + " sample hit: " + (i * samplesPerPixel + sampleWaveFormOffset));
				g2d.drawLine(xOffset + additionalXOffset + xStart, yOffset - yStart, xOffset + additionalXOffset + xEnd, yOffset - yEnd);
				yStart = yEnd;
				xStart++;
				if (xEnd > getWidth()) break;
			}
		}
		*/
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
			value += values[startingPoint + index];
		}
		return value/nbSamples;
	}
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
		g2dText.drawLine(xOffset, 0, xOffset, getHeight());//y axis
		g2dText.drawLine(0, yOffset, getWidth(), yOffset);//x axis

		g2dText.setColor(new Color(255, 255, 255, 255));

		
		double pixMCTB;//Represents the pixel that is a multiple of "pixelsPerStep" which is the closest to the border
		int xPos;
		int yPos;
		//Draws the unit increments on the x axis--------------------------------------------------------------------------------------
		double pixelsPerXStep = xPixelsPerUnit * xScale;//Amount of pixel in-between white lines
		pixMCTB = -(xOffset - ((xOffset % pixelsPerXStep) + pixelsPerXStep) % pixelsPerXStep);
		i = 0;
		do {
			xPos = xOffset + (int) pixMCTB + (int) (pixelsPerXStep*i);
			yPos = yOffset;
			printXCenteredString((Math.round((pixMCTB + pixelsPerXStep*i)/xPixelsPerUnit*10000)/10000.0) + "", xPos, yPos, g2dText);
			g2dSoftLines.drawLine(xPos, 0, xPos, getHeight());
			i++;
		}	while(pixelsPerXStep*i < getWidth());

		//Draws the unit increments on the y axis--------------------------------------------------------------------------------------
		double pixelsPerYStep = yPixelsPerUnit * yScale;//Amount of pixel in-between white lines
		pixMCTB = -(yOffset - ((yOffset % pixelsPerYStep) + pixelsPerYStep) % pixelsPerYStep);
		i = 0;
		do {
			if (-pixMCTB - pixelsPerYStep*i < EPSILON && -pixMCTB - pixelsPerYStep*i > -EPSILON) {
				i++;
				continue;
			}
			xPos = xOffset;
			yPos = yOffset + (int) pixMCTB + (int) (pixelsPerYStep*i);
			printYCenteredString((Math.round((-pixMCTB - pixelsPerYStep*i)/yPixelsPerUnit*10000)/10000.0) + "", xPos, yPos, g2dText);
			g2dSoftLines.drawLine(0, yPos, getWidth(), yPos);
			i++;
		}	while(pixelsPerYStep*i < getHeight());
	}
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
		int zoomDirection = e.getWheelRotation();//(- -> in ; + -> out)
		int modifier = e.getModifiersEx();//64 for Shift, 128 for Ctrl and 0 for nothing
		
		if (modifier == 64) zoomY(zoomDirection);
		else if (modifier == 128) zoomX(zoomDirection);
		else zoomAll(zoomDirection);
		
	}
	/**
	 * Zooms all axes
	 */
	public void zoomAll(int zoomDirection) {
		zoomX(zoomDirection);
		zoomY(zoomDirection);
		
	}
	/**
	 * Zooms only the x axis
	 */
	public void zoomX(int zoomDirection) {
		xZoomAmount += zoomDirection;
		adjustThresholdsTight("x");
		
		//Adjusts the scale if too zoomed in or out for the x axis
		if (Math.pow(zoomPercentage, xZoomAmount) >= xZoomInThreshold) {//Too zoomed in
			if (((xDivAmountByTwo + xDivAmountByFiveHalf) % 3 + 3) % 3 == 1) {
				xDivAmountByFiveHalf++;
			}
			else {
				xDivAmountByTwo++;
			}
		}
		else if (Math.pow(zoomPercentage, xZoomAmount) < xZoomOutThreshold) {//Too zoomed out
			if (((xDivAmountByTwo + xDivAmountByFiveHalf) % 3 + 3) % 3 == 2) {
				xDivAmountByFiveHalf--;
			}
			else {
				xDivAmountByTwo--;
			}
		}
		
		//Adjusts the scale
		xScale = Math.pow(2, -xDivAmountByTwo) * Math.pow(2.5, -xDivAmountByFiveHalf);

		//Manages the amount of pixels per unit
		xPixelsPerUnit = startingPixelsPerUnit * Math.pow(zoomPercentage, xZoomAmount);

		//Gives a bit of info on the panel
		lbl_pixPerUX.setText("Pixels per unit, X axis: " + Math.round((xPixelsPerUnit * 1000)) / 1000.0);
		
	}
	/**
	 * Zooms only the y axis
	 */
	public void zoomY(int zoomDirection) {
		yZoomAmount += zoomDirection;
		adjustThresholdsTight("y");

		//Adjusts the scale if too zoomed in or out for the y axis
		if (Math.pow(zoomPercentage, yZoomAmount) >= yZoomInThreshold) {//Too zoomed in
			if (((yDivAmountByTwo + yDivAmountByFiveHalf) % 3 + 3) % 3 == 1) {
				yDivAmountByFiveHalf++;
			}
			else {
				yDivAmountByTwo++;
			}
		}
		else if (Math.pow(zoomPercentage, yZoomAmount) < yZoomOutThreshold) {//Too zoomed out
			if (((yDivAmountByTwo + yDivAmountByFiveHalf) % 3 + 3) % 3 == 2) {
				yDivAmountByFiveHalf--;
			}
			else {
				yDivAmountByTwo--;
			}
		}

		//Adjusts the scale
		yScale = Math.pow(2, -yDivAmountByTwo) * Math.pow(2.5, -yDivAmountByFiveHalf);

		//Manages the amount of pixels per unit
		yPixelsPerUnit = startingPixelsPerUnit * Math.pow(zoomPercentage, yZoomAmount);

		//Gives a bit of info on the panel
		lbl_pixPerUY.setText("Pixels per unit, Y axis: " + Math.round((yPixelsPerUnit * 1000)) / 1000.0);
	}
	
	/**
	 * Adjusts the zoom limits after a scale change. Works by anticipating the next value used to multiply.
	 * The looseness of these thresholds make zooming less frequent
	 */
	/*
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
			divAmountByTwo = xDivAmountByTwo;
			divAmountByFiveHalf = xDivAmountByFiveHalf;
		}
		else {
			divAmountByTwo = yDivAmountByTwo;
			divAmountByFiveHalf = yDivAmountByFiveHalf;
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
			xZoomOutThreshold = zoomOutThreshold;
			xZoomInThreshold = zoomInThreshold;
		}
		else {
			yZoomOutThreshold = zoomOutThreshold;
			yZoomInThreshold = zoomInThreshold;
		}
	}

	/**
	 * Sets the info of the wav file
	 * @param wavInfo Object that contains the information about the wav file
	 */
	public void setWavInfo(WavInfo wavInfo) {
		this.wavInfo = wavInfo;
	}
}