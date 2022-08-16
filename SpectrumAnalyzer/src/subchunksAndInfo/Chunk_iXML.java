package subchunksAndInfo;

import wavParsingAndStoring.WavInfo;

public class Chunk_iXML extends SubChunks{
	public Chunk_iXML(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}