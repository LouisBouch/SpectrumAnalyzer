package subchunksAndInfo;

public class Chunk_XxXx extends SubChunks{

	public Chunk_XxXx(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}

}
