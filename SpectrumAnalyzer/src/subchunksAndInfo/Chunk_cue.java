package subchunksAndInfo;

public class Chunk_cue extends SubChunks {
	public Chunk_cue(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}
