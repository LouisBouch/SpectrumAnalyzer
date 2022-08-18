package subchunkObjects;

import interfaces.Info;

public class FactInfo implements Info {
	/**
	 * Amount of sample frames/block in the data file
	 */
	private int nbSampleFrames;
	
	public int getNbSampleFrames() {
		return nbSampleFrames;
	}

	public void setNbSampleFrames(int nbSampleFrames) {
		this.nbSampleFrames = nbSampleFrames;
	}

	@Override
	public String toString() {
		String factInfo = "<B>Additional file info:</B><br/>";
		factInfo += "Number of sample frames: " + nbSampleFrames + " frames";
		return factInfo;
	}//End toString
}
