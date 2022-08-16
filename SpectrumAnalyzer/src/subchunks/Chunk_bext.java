package subchunks;

import wavParsingAndStoring.WavInfo;

public class Chunk_bext extends SubChunks{

	public Chunk_bext(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}

}
