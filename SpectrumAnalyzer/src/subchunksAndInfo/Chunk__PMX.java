package subchunksAndInfo;

import wavParsingAndStoring.WavInfo;

public class Chunk__PMX  extends SubChunks{
	public Chunk__PMX(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}

}