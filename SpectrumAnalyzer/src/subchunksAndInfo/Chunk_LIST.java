package subchunksAndInfo;

public class Chunk_LIST extends SubChunks{
	public Chunk_LIST(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}
