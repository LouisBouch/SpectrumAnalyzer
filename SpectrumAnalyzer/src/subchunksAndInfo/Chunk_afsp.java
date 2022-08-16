package subchunksAndInfo;

import wavParsingAndStoring.WavInfo;

public class Chunk_afsp extends SubChunks{
	public Chunk_afsp(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}
