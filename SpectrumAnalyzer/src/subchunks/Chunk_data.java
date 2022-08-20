package subchunks;

import subchunkObjects.DataInfo;
import wavParsingAndStoring.WavInfo;

public class Chunk_data extends SubChunks {
	
	private DataInfo data = new DataInfo();

	public Chunk_data(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}
	
	/**
	 * Sets the info for the current subchunk
	 */
	@Override
	public void setInfo() {
		data.setData(getData());
		this.getInfoReservoir().setDataInfo(data);
		this.getInfoReservoir().setWeight(this.getData().length);
	}
	
	@Override
	public String toString() {
		return "<B>subchunk:</B> " + this.getSubChunkName();
	}

}
//https://wavefilegem.com/how_wave_files_work.html