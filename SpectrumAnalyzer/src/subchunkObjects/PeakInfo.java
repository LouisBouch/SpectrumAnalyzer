package subchunkObjects;

import java.util.ArrayList;

import interfaces.Info;
import tools.StringTools;

public class PeakInfo implements Info {
	private ArrayList<Peak> peaks = new ArrayList<Peak>();
	
	public ArrayList<Peak> getPeaks() {
		return peaks;
	}

	public void setPeaks(ArrayList<Peak> peaks) {
		this.peaks = peaks;
	}
	/**
	 * Adds a peak to the list
	 * @param peakValue The value of the peak (0 to 1)
	 * @param sampleNb The sample number at which the peak occurs
	 * @param channel The peak's channel
	 * @param time Time at which the peak occurs
	 */
	public void addPeak(double peakValue, int sampleNb, String channel, double time) {
		peaks.add(new Peak(peakValue, sampleNb, channel, time));
	}
	/**
	 * Adds a peak to the list
	 * @param peak The peak
	 */
	public void addPeak(Peak peak) {
		peaks.add(peak);
	}
	@Override
	public String toString() {
		String peakInfo = "<B>Peak info:</B>";
		peakInfo += StringTools.arrayListToString(peaks);
		return peakInfo;
	}//End toString
}
