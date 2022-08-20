package subchunkObjects;

import tools.ValueParsingTools;

public class Cue {
	/**
	 * If a play list chunk exists, this value stores the byte position of the data/silent chunk that contains the cue. Otherwise, stores 0.
	 */
	private int chunkStart;
	/**
	 * Stores the byte position in the data chunk where the cue occurs.
	 */
	private int cuePos;
	/**
	 * Time in seconds at which the cue happens
	 */
	private double cueTime;
	/**
	 * Creates a cue
	 * @param chunkStart The chunk's starting position where the cue is contained
	 * @param cuePos The sample in which the cue belongs
	 * @param cueTime The time at which the cue happens
	 */
	public Cue(int chunkStart, int cuePos, double cueTime) {
		this.chunkStart = chunkStart;
		this.cuePos = cuePos;
		this.cueTime = cueTime;
	}
	
	public int getChunkStart() {
		return chunkStart;
	}

	public void setChunkStart(int chunkStart) {
		this.chunkStart = chunkStart;
	}

	public int getCuePos() {
		return cuePos;
	}

	public void setCuePos(int cuePos) {
		this.cuePos = cuePos;
	}

	public double getCueTime() {
		return cueTime;
	}

	public void setCueTime(int cueTime) {
		this.cueTime = cueTime;
	}

	@Override
	public String toString() {
		return "Cue at " + ValueParsingTools.refinedTime(cueTime);
	}
}
