package subchunkObjects;

import java.util.ArrayList;

import interfaces.Info;

public class CueInfo implements Info {
	private ArrayList<Cue> cues = new ArrayList<>();
		/**
		 * Adds a cue to the list of cues
		 * @param id The unique id of the cue
		 * @param cuePosInTime When the cue happens
		 * @param cueChannel To which channel the cue belongs
		 * @return Returns the added cue
		 */
	public Cue addCue(int id, int cuePosInTime, String cueChannel) {
		Cue cue = new Cue(id, cuePosInTime, cueChannel);
		cues.add(cue);
		return cue;
	}
	/**
	 * Adds a cue to the list of cues
	 */
	public Cue addCue(Cue cue) {
		cues.add(cue);
		return cue;
	}
	
	public ArrayList<Cue> getCues() {
		return cues;
	}


	public void setCues(ArrayList<Cue> cues) {
		this.cues = cues;
	}


	@Override
	public String toString() {
		String cueString = "<B>Audio cues:</B><br/>";
		for (int cue = 0; cue < cues.size(); cue++) {
			cueString += cues.get(cue).toString();
		}
		return cueString;
	}//End toString
}
