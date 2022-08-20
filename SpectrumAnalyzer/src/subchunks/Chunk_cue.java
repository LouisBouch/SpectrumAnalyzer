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
//		info = "";
		int nbCues = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), 0, 4, ByteManipulationTools.LITTLEENDIAN);
		System.out.println(ByteManipulationTools.getDecimalValueUnsigned(getData(), 4, 4, ByteManipulationTools.LITTLEENDIAN));
		int byteOffset = 4;
		for (int cue = 0; cue < nbCues; cue++) {
			int id = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
			byteOffset += 16;
			int blockStartInByte = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
			int sampleNb = blockStartInByte / this.getInfoReservoir().getFormatInfo().getBlockAlign();
			int cueTime = sampleNb / this.getInfoReservoir().getFormatInfo().getSampleRate();
			byteOffset += 4;
			int blockOffset = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
			int channelNb = blockOffset / (this.getInfoReservoir().getFormatInfo().getBitsPerSample() / 8);
			String channel = this.getInfoReservoir().getFormatInfo().getChannelsLocationLongName()[channelNb];
			cueInfo.addCue(id, cueTime, channel);
		}//End for loop
		if (nbCues > 0) this.getInfoReservoir().setCueInfo(cueInfo);
	}//End setInfo
}
//https://www.recordingblogs.com/wiki/cue-chunk-of-a-wave-file