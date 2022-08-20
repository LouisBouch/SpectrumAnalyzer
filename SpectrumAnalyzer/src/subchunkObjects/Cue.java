package subchunkObjects;

import tools.ValueParsingTools;

public class Cue {
	/**
	 * The unique id of the cue
	 */
	private int id;
	/**
	 * When the cue happens
	 */
	private int cuePosInTime;
	/**
	 * To which channel the cue belongs
	 */
	private String cueChannel;
	/**
	 * Creates a cue
	 * @param id The unique id of the cue
	 * @param cuePosInTime When the cue happens
	 * @param cueChannel To which channel the cue belongs
	 */
	public Cue(int id, int cuePosInTime, String cueChannel) {
		this.id = id;
		this.cuePosInTime = cuePosInTime;
		this.cueChannel = cueChannel;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCuePosInTime() {
		return cuePosInTime;
	}

	public void setCuePosInTime(int cuePosInTime) {
		this.cuePosInTime = cuePosInTime;
	}

	public String getCueChannel() {
		return cueChannel;
	}

	public void setCueChannel(String cueChannel) {
		this.cueChannel = cueChannel;
	}

	@Override
	public String toString() {
		String cueString = "Cue at " + ValueParsingTools.refinedTime(cuePosInTime) + " on " + cueChannel + " channel<br/>";
		return cueString;
	}//End toString
}
