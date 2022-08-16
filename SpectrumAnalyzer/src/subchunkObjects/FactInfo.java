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
		return "";
	}//End toString
}
