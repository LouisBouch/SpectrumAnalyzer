package soundProcessing;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import subchunkObjects.FormatInfo;
import wavParsingAndStoring.WavInfo;

public class AudioPlayback {
	private WavInfo wavInfo;
	private Clip clip;
	private byte[] data;
	private boolean playing = false;
	private double playBackSpeed = 1;
	
	/**
	 * Creates empty object
	 */
	public AudioPlayback() {
		
	}
	/**
	 * Creates a new AudioPlayBack object
	 * @param wavInfo Information about the wav file
	 * @param clip The audio line to use
	 */
	public AudioPlayback(WavInfo wavInfo, Clip clip) {
		this.wavInfo = wavInfo;
		this.clip = clip;
	}
	/**
	 * Creates a new AudioPlayBack object
	 * @param wavInfo Information about the wav file
	 * @param clip The audio line to use
	 * @param data The audio samples
	 */
	public AudioPlayback(WavInfo wavInfo, Clip clip, byte[] data) {
		this.wavInfo = wavInfo;
		this.clip = clip;
		this.data = data;
	}
	/**
	 * Creates a new AudioPlayBack object
	 * @param playBack The AudioPlayBack object
	 */
	public AudioPlayback(AudioPlayback playBack) {
		wavInfo = playBack.getWavInfo();
		clip = playBack.getClip();
		data = playBack.getData();
	}
	
	/**
	 * Plays the samples
	 */
	public void play() {
		if (clip != null && wavInfo != null) {
			if (!clip.isOpen()) {
				try {
					FormatInfo formatInfo = wavInfo.getFormatInfo();
					boolean signed = formatInfo.getBitsPerSample() <= 8 ? false : true;
					AudioFormat format = new AudioFormat((int) (formatInfo.getSampleRate() * playBackSpeed), formatInfo.getBlockAlign() * 8 / formatInfo.getNbChannels(), formatInfo.getNbChannels(), signed, false);
					
					clip.open(format, data, 0, wavInfo.getDataInfo().getData().length);
					clip.start();
					playing = true;
				}
				catch (LineUnavailableException e) {
					System.out.println(e);
				}
			}
		}
	}//End play
	
	/**
	 * Stops the play back
	 */
	public void stop() {
		if (clip.isOpen()) {
			clip.close();
			playing = false;
		}
	}
	
	public byte[] getData() {
		return data;
	}
	/**
	 * Sets the sample values to be played
	 * @param bytes The samples
	 */
	public void setData(byte[] data) {
		this.data = data; 
	}//End setData

	public WavInfo getWavInfo() {
		return wavInfo;
	}
	public void setWavInfo(WavInfo wavInfo) {
		this.wavInfo = wavInfo;
	}

	public Clip getClip() {
		return clip;
	}
	public void setClip(Clip clip) {
		this.clip = clip;
	}
	public boolean isPlaying() {
		return playing;
	}
	public double getPlayBackSpeed() {
		return playBackSpeed;
	}
	
	
}
