package subchunksAndInfo;

public class Chunk_afsp extends SubChunks{
	public Chunk_afsp(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}
