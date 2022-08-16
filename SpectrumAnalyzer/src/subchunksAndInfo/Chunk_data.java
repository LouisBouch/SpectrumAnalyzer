package subchunksAndInfo;

import subChunkObjects.DataInfo;
import wavParsingAndStoring.WavInfo;

public class Chunk_data extends SubChunks {
	
	private DataInfo data = new DataInfo();

	public Chunk_data(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();
	}
	
	/**
	 * Sets the info for the current subchunk
	 */
	public void setInfo() {
//		this.setInfo("Data can be read from the waveform analyzer ---->");
		data.setData(getData());
		this.getInfoReservoir().setDataInfo(data);
	}
	
	@Override
	public String toString() {
		return "<B>subchunk:</B> " + this.getSubChunkName() + "<br/>" + this.getInfo();
	}

}
//https://wavefilegem.com/how_wave_files_work.html