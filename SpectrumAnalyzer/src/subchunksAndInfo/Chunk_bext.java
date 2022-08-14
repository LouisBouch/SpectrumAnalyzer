package subchunksAndInfo;

public class Chunk_bext extends SubChunks{

	public Chunk_bext(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}

}
