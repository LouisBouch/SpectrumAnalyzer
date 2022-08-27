package plotting;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class LoopSection extends JComponent{
	private static final long serialVersionUID = 4003632832848322760L;
	
	private double firstSample;
	private double lastSample;
	
	
	private Plot plot;
	
	/**
	 * Creates a loop section
	 * @param plot
	 */
	public LoopSection(Plot plot) {
		this.plot = plot;
	}
	
	/**
	 * Paints the loop section
	 * @param g2d The graphics element
	 */
	public void paint(Graphics2D g2d) {
		g2d.setColor(new Color(255, 255, 255, 50));
		int pixStart = (int) (firstSample/plot.getSamplesPerPixel());
		int pixEnd = (int) (lastSample/plot.getSamplesPerPixel());
		int loopWidthPix = pixEnd - pixStart;
		if (loopWidthPix < 0) loopWidthPix = 0;
		//Loop area
		g2d.fillRect(plot.getxOffset() + (int) (firstSample/plot.getSamplesPerPixel()), 0, 
				loopWidthPix, plot.getPreferredSize().height);
		//Delimitation lines of the loop
		g2d.setColor(new Color(200, 200, 200, 200));
		g2d.drawLine(plot.getxOffset() + pixStart, 0, plot.getxOffset() + pixStart, plot.getPreferredSize().height);
		g2d.drawLine(plot.getxOffset() + pixEnd, 0, plot.getxOffset() + pixEnd, plot.getPreferredSize().height);
		
	}
	
	/**
	 * Get amount of frames to loop
	 * @return Returns the amount of samples in the loop
	 */
	public int getLoopSampleSize() {
		return (int) (lastSample - firstSample);
	} 
	public double getFirstSample() {
		return firstSample;
	}
	public double getLastSample() {
		return lastSample;
	}
	public void setFirstSample(double firstSample) {
		this.firstSample = firstSample < 0 ? 0 : firstSample;
		if (this.firstSample > lastSample) setLastSample(this.firstSample);
	}
	public void setLastSample(double lastSample) {
		this.lastSample = lastSample > plot.getNbSamples() ? plot.getNbSamples() : lastSample;
		if (this.lastSample < firstSample) setFirstSample(this.lastSample);
	}
	
	

}
