package subchunks;

import subchunkObjects.CueInfo;
import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_cue extends SubChunks {
	private CueInfo cueInfo = new CueInfo();
	
//	private String info;
	
	public Chunk_cue(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();
	}//End Chunk_cue
	/**
	 * Manages the cue points
	 */
	public void setInfo() {
		int nbCues = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), 0, 4, ByteManipulationTools.LITTLEENDIAN);
		int byteOffset = 4;
		for (int cue = 0; cue < nbCues; cue++) {
			int id = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
			byteOffset += 12;
			int chunkStart = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
			byteOffset += 8;
			int cuePos = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
			double cueTime = cuePos * 1.0 / this.getInfoReservoir().getFormatInfo().getSampleRate();
			byteOffset += 4;
			cueInfo.addCue(id, chunkStart, cuePos, cueTime);
		}//End for loop
		if (nbCues > 0) this.getInfoReservoir().setCueInfo(cueInfo);
	}//End setInfo
}
//https://www.recordingblogs.com/wiki/cue-chunk-of-a-wave-file