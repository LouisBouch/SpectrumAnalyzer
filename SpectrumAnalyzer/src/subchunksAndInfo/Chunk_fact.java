package subchunksAndInfo;

import subChunkObjects.FactInfo;
import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_fact extends SubChunks{
	
	private FactInfo factInfo = new FactInfo();
	
	public Chunk_fact(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();	
	}
	/**
	 * Sets the info for the current subchunk
	 */
	public void setInfo() {
		int[] temp = new int[this.getSubChunkSize()];
		for (int i = 0; i < this.getSubChunkSize(); i++) {
			if (this.getData()[i] < 0) temp[i] = (int) ByteManipulationTools.unsignedVersionOfByteTwosComplement(this.getData()[i]);
			else temp[i] = this.getData()[i];
		}
		
		int nbSampleFrames = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 0, 4, ByteManipulationTools.LITTLEENDIAN);
		factInfo.setNbSampleFrames(nbSampleFrames);
//		info += "Number of sample frames: " + nbSampleFrames;
		
//		this.setInfo(info);
		this.getInfoReservoir().setFactInfo(factInfo);
	}
//	//Getters for the different properties of the fact chunk
//	public int getNbSampleFrames() {
//		return nbSampleFrames;
//	}
	
}
//https://wavefilegem.com/how_wave_files_work.html