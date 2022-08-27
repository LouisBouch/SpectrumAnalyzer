package plotting;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class AudioBar extends JComponent{
	
	private static final long serialVersionUID = 4351930063200370359L;
	
	private int sampleNb;
	private int pixelOffset;
	private double timeOffset;
	
	private Plot plot;
	
	/**
	 * Creates an audio bar
	 * @param plot The plot which has the audio bar
	 */
	public AudioBar(Plot plot) {
		this.plot = plot;
	}
	/**
	 * Paints the bar
	 * @param g2d The graphics element
	 */
	public void paint(Graphics2D g2d) {
		Color col = g2d.getColor();
		g2d.setColor(Color.white);
		
		pixelOffset = (int) Math.round(timeOffset * plot.getxPixelsPerUnit());
		g2d.drawLine(plot.getxOffset() + pixelOffset, 0, plot.getxOffset() + pixelOffset, plot.getPreferredSize().height);
		
		g2d.setColor(col);
	}
	
	public void setSampleNb(int sampleNb) {
		this.sampleNb = sampleNb;
	}
	public void setPlot(Plot plot) {
		this.plot = plot;
	}
	public void setTimeOffset(double timeOffset) {
		this.timeOffset = timeOffset;
	}
	public int getSampleNb() {
		return sampleNb;
	}
	
	
}
