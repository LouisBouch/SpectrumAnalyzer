package subchunksAndInfo;

import tools.ByteManipulationTools;

public class Chunk_fact extends SubChunks{
	private int nbSampleFrames;
	private String info;
	
	public Chunk_fact(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();	
	}
	/**
	 * Sets the info for the current subchunk
	 */
	public void setInfo() {
		info = "";
		int[] temp = new int[this.getSubChunkSize()];
		for (int i = 0; i < this.getSubChunkSize(); i++) {
			if (this.getData()[i] < 0) temp[i] = (int) ByteManipulationTools.unsignedVersionOfByteTwosComplement(this.getData()[i]);
			else temp[i] = this.getData()[i];
		}
		
		nbSampleFrames = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 0, 4, ByteManipulationTools.LITTLEENDIAN);
		info += "Number of sample frames: " + nbSampleFrames;
		
		this.setInfo(info);
	}
	//Getters for the different properties of the fact chunk
	public int getNbSampleFrames() {
		return nbSampleFrames;
	}
	
}
