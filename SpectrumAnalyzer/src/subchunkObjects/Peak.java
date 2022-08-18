package subchunkObjects;

import tools.NumberManipulationTools;
import tools.ValueParsingTools;

public class Peak {
	/**
	 * Value of the peak between 0 and 1
	 */
	private double peakValue;
	/**
	 * Sample number at which the peak occurs
	 */
	private int sampleNb;
	/**
	 * The peak's channel
	 */
	private String channel;
	/**
	 * The time at which the peak occurs
	 */
	private double time;
	
	/**
	 * Creates a new peak
	 * @param peakValue The value of the peak (0 to 1)
	 * @param sampleNb The sample number at which the peak occurs
	 * @param channel The peak's channel
	 * @param time Time at which the peak occurs
	 */
	public Peak(double peakValue, int sampleNb, String channel, double time) {
		this.peakValue = peakValue;
		this.sampleNb = sampleNb;
		this.channel = channel;
		this.time = time;
	}
	
	public double getPeakValue() {
		return peakValue;
	}

	public void setPeakValue(double value) {
		this.peakValue = value;
	}

	public int getSampleNb() {
		return sampleNb;
	}

	public void setSampleNb(int sampleNb) {
		this.sampleNb = sampleNb;
	}

	public String getchannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public double getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return channel + " channel peaks at " + NumberManipulationTools.setDecimalPlaces(peakValue, 3) + " near " + ValueParsingTools.refinedTime(time);
	}
}
