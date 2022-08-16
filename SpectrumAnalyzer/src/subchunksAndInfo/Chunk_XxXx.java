package subchunksAndInfo;

import wavParsingAndStoring.WavInfo;

public class Chunk_XxXx extends SubChunks{

	public Chunk_XxXx(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}

}
