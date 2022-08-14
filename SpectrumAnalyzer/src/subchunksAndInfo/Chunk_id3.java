package subchunksAndInfo;

public class Chunk_id3 extends SubChunks{
	public Chunk_id3(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}
