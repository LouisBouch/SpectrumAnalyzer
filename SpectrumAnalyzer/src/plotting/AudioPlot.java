package plotting;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import soundProcessing.AudioPlayback;

public class AudioPlot extends Plot implements Runnable {

	private static final long serialVersionUID = -751338344194399909L;
	/**Contains audio information*/
	private AudioPlayback audio;
	/**The visual cue representing the play back position*/
	private AudioBar bar = new AudioBar(this);
	/**The section to loop*/
	private LoopSection loop = new LoopSection(this);
	/**Contains the play back speed*/
	private double playBackSpeed = 1;
	/**True if running, false otherwise*/
	private boolean running = false;
	/**True if the sound has been closed*/
	private boolean stopped = true;
	/**True if the audio is set to loop*/
	private boolean looping = false;
	
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
		super.loadWave(values, samplesPerUnit, plotsLegend);
		loop = new LoopSection(this);
		this.audio = audio;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getModifiersEx() == 1088) {
					System.out.println("shift");
					if (!running && stopped) loop.setFirstSample((e.getX() - getxOffset()) * getSamplesPerPixel());
				}
				if (e.getModifiersEx() == 1152) {
					System.out.println("ctrl");
					if (!running && stopped) loop.setLastSample((e.getX() - getxOffset()) * getSamplesPerPixel());
				}
				repaint();
			}
		});//End addMouseListener
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (bar != null) bar.paint(g2d);
		if (loop != null && looping) loop.paint(g2d);
	}//End paintComponent
	/**
	 * Runs the audio bar
	 */
	@Override
	public void run() {
		//Time initialization
		double iniTime = looping && loop.getLoopSampleSize() > 0 ? System.nanoTime() + (loop.getFirstSample() / getSamplesPerUnit()) * 1E9 : System.nanoTime();
		double initialTimeOffset = looping && loop.getLoopSampleSize() > 0 ? loop.getFirstSample() / getSamplesPerUnit() : 0;
		double initialSampleOffset = looping && loop.getLoopSampleSize() > 0 ? loop.getFirstSample() : 0;
		//Makes sure everything starts at the same time
		while (audio.getClip().getFramePosition() == initialSampleOffset) {
			sleep(5);
		}
		//Checks offset values
		double timeElapsed = (System.nanoTime()-iniTime)*playBackSpeed*1E-9;
		double offset = audio.getClip().getMicrosecondPosition()*playBackSpeed*1E-6 - timeElapsed;
		//Adjusts for the offset to make sure the bar is synchronized to the audio
		iniTime -= offset / playBackSpeed * 1E9;
		timeElapsed = (System.nanoTime()-iniTime)*playBackSpeed*1E-9;
		
		//Checks how many times it has been looped
		long timesLooped = looping && loop.getLoopSampleSize() > 0 ? (int) ((timeElapsed - initialTimeOffset) / (loop.getLoopSampleSize() / getSamplesPerUnit())) : 0;
		int rep = 0;//Amount of times there has been a loop % 100
		
		//Adjusts the bar and repaints
		while(running) {
			//Makes sure the bar is synchronized to the audio
			if (rep == 0) {
				offset = audio.getClip().getMicrosecondPosition()*playBackSpeed*1E-6 - timeElapsed;
				//Shifts the initial time by the necessary amount to make do for the offset
				if (Math.abs(offset) > 0.05) {
					iniTime -= offset / playBackSpeed * 1E9;
					System.out.println("offset");
				}
			}
			//Sets the bar offset
			timeElapsed = (System.nanoTime()-iniTime)*playBackSpeed*1E-9;
			bar.setTimeOffset(timeElapsed - timesLooped*loop.getLoopSampleSize()/getSamplesPerUnit());
			
			//Listens for the end of the loop
			if (looping && loop.getLoopSampleSize() > 0 && (timeElapsed - initialTimeOffset) * getSamplesPerUnit() >= loop.getLoopSampleSize() * (timesLooped + 1)) {
				timesLooped = (int) ((timeElapsed - initialTimeOffset) / (loop.getLoopSampleSize() / getSamplesPerUnit()));
				bar.setTimeOffset(loop.getFirstSample() / getSamplesPerUnit());
			}
			
			//Stops the audio bar if it reaches the end
			if (!(looping && loop.getLoopSampleSize() > 0) && audio.getClip().getFramePosition() > audio.getClip().getFrameLength() - 1) {
				System.out.println("Reached end");
				stop();
			}
			
			//End of loop actions
			repaint();
			sleep(10);
			rep = (rep+1) % 100;
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
			
			if (audio.getClip().getMicrosecondPosition() == 0) playBackSpeed = audio.getPlayBackSpeed();//If starting from 0, sets the playback speed
			if (looping && loop.getLoopSampleSize() > 0) audio.loop((int) (loop.getFirstSample()), (int) (loop.getLastSample()));
			else audio.play();
			
			final Thread thread = new Thread(this);
			thread.start();
		}
	}//End start
	/**
	 * Pauses the repaints
	 */
	public void pause() {
		if (running) running = false;
		audio.pause();
	}//End pause
	/**
	 * Stops the repaints
	 */
	public void stop() {
		if (audio != null && audio.getClip() != null) {
			audio.stop();
			if (running) running = false;
			bar.setTimeOffset(0);

			stopped = true;
			repaint();
		}
	}//End stop
	public AudioPlayback getAudio() {
		return audio;
	}
	public void setPlayBackSpeed(double playBackSpeed) {
		this.playBackSpeed = playBackSpeed;
	}
	public void setAudio(AudioPlayback audio) {
		this.audio = audio;
	}
	public void setLooping(boolean looping) {
		stop();
		if (!running) this.looping = looping;
		repaint();
	}
	
}
