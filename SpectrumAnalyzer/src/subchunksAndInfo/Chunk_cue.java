package subchunksAndInfo;

import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_cue extends SubChunks {
	private String info;
	
	private int nbCues;
	private int[] chunkStart;
	private int[] blockStart;
	private int[] sampleStart;
	
	public Chunk_cue(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();
	}//End Chunk_cue
	/**
	 * Manages the cue points
	 */
	public void setInfo() {
		info = "";
		nbCues = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), 0, 4, ByteManipulationTools.LITTLEENDIAN);
		int byteOffset = 4;
		for (int cue = 0; cue < nbCues; cue++) {
			System.out.println("ID: " + ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN));
			byteOffset += 4;
			System.out.println("Sample position: " + ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN));
		}//End for loop
	}//End setInfo
}
//https://www.recordingblogs.com/wiki/cue-chunk-of-a-wave-file