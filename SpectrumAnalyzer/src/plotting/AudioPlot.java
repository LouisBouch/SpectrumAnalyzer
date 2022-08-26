package plotting;

import java.awt.Graphics;
import java.awt.Graphics2D;

import soundProcessing.AudioPlayback;

public class AudioPlot extends Plot implements Runnable {

	private static final long serialVersionUID = -751338344194399909L;
	/**Contains audio information*/
	private AudioPlayback audio;
	/**The visual cue representing the play back position*/
	private AudioBar bar = new AudioBar(this);
	/**Contains the play back speed*/
	private double playBackSpeed = 1;
	/**True if running, false otherwise*/
	private boolean running = false;
	/**True if the sound has been closed*/
	private boolean stopped = true;
	
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
	/**
	 * Creates and empty audio plot
	 */
	public AudioPlot() {
		super();
	}
	/**
	 * Loads information about the audio file and plot
	 * @param values The data from the wav file
	 * @param samplesPerUnit The amount of samples required to make one unit (x axis)
	 * @param plotsLegend A legend of each plot. Each array item contains information about the name of the plot
	 * @param audio Contains the audio clip and everything necessary to get the bar running
	 */
	public void loadWave(double[][] values, double samplesPerUnit, String[] plotsLegend, AudioPlayback audio) {
		this.audio = audio;
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (bar != null) bar.paint(g2d);
	}//End paintComponent
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
	}//End run
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
	}//ENd start
	/**
	 * Pauses the repaints
	 */
	public void pause() {
		if (running) running = false;
	}//End pause
	/**
	 * Stops the repaints
	 */
	public void stop() {
		if (running) running = false;
		bar.setTimeOffset(0);
		stopped = true;
		repaint();
	}//End stop
	public void setPlayBackSpeed(double playBackSpeed) {
		this.playBackSpeed = playBackSpeed;
	}
	public AudioPlayback getAudio() {
		return audio;
	}
	public void setAudio(AudioPlayback audio) {
		this.audio = audio;
	}
}
