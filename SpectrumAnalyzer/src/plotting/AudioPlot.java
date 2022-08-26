package plotting;

import soundProcessing.AudioPlayback;

public class AudioPlot extends Plot {

	private static final long serialVersionUID = -751338344194399909L;
	
	/**
	 * Creates an AudioPlot object. Same as a plot, but has the ability to run a audio progress bar through its data
	 * @param values The data from the wav file
	 * @param samplesPerUnit The amount of samples required to make one unit (x axis)
	 * @param plotsLegend A legend of each plot. Each array item contains information about the name of the plot
	 * @param audio Contains the audio clip and everything necessary to get the bar running
	 */
	public AudioPlot(double[][] values, double samplesPerUnit, String[] plotsLegend, AudioPlayback audio) {
		super(values, samplesPerUnit, plotsLegend);
		loadWave(values, samplesPerUnit, plotsLegend, audio);
	}
	public void loadWave(double[][] values, double samplesPerUnit, String[] plotsLegend, AudioPlayback audio) {
		
	}
}
