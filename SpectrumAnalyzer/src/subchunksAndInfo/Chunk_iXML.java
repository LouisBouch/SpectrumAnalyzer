package subchunksAndInfo;

public class Chunk_iXML extends SubChunks{
	public Chunk_iXML(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
}