package subchunkObjects;

import java.util.HashMap;

import interfaces.Info;

public class CueInfo implements Info {
//	private ArrayList<Cue> cues = new ArrayList<>();
	private HashMap<Integer, Cue> cues = new HashMap<>();
		/**
		 * Adds a cue to the map
		 * @param id THe unique id of the cue
		 * @param chunkStart The chunk's starting position where the cue is contained
		 * @param cuePos The sample in which the cue belongs
		 * @param cueTime The time at which the cue happens
		 * @return Returns the added cue
		 */
	public Cue addCue(int id, int chunkStart, int cuePos, double cueTime) {
		Cue cue = new Cue(chunkStart, cuePos, cueTime);
		cues.put(id, cue);
		return cue;
	}
	/**
	 * Adds a cue to the list of cues
	 */
	public Cue addCue(int id, Cue cue) {
		cues.put(id, cue);
		return cue;
	}
	
	public HashMap<Integer, Cue> getCues() {
		return cues;
	}


	public void setCues(HashMap<Integer, Cue> cues) {
		this.cues = cues;
	}
	/**
	 * Returns the cue with the correct id
	 * @param id The cue id
	 * @return The cue
	 */
	public Cue getCue(int id) {
		return cues.get(id);
	}
	@Override
	public String toString() {
		String cueInfo = "Cues:" + cues;
		return cueInfo;
	}
}
