package subchunksAndInfo;

public class Chunk_data extends SubChunks {

	public Chunk_data(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();
	}
	
	/**
	 * Sets the info for the current subchunk
	 */
	public void setInfo() {
		this.setInfo("Data can be read from the waveform analyzer ---->");
	}
	
	@Override
	public String toString() {
		return "<B>subchunk:</B> " + this.getSubChunkName() + "<br/>" + this.getInfo();
	}

}
