package subchunksAndInfo;

import tools.ByteManipulationTools;
import tools.NumberManipulationTools;

public class Chunk_PEAK extends SubChunks {
	private String info;
	
	private double version;
	private double timeStamp;
	
	private double[][] peaks;//[nbChannels][PeakValue]
	
	public Chunk_PEAK(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();
		
	}//End Chunk_PEAK
	public void setInfo() {
		info = "";
		version = ByteManipulationTools.getDecimalValueUnsigned(getData(), 0, 4, ByteManipulationTools.LITTLEENDIAN);
		timeStamp = ByteManipulationTools.getDecimalValueUnsigned(getData(), 4, 4, ByteManipulationTools.LITTLEENDIAN);
		int nbChannels = (getData().length - 8) / 8;
		peaks = new double[nbChannels][2];
		for (int channel = 0; channel < nbChannels; channel++) {
			peaks[channel][0] = ByteManipulationTools.getFloatingP32(getData(), 8 + 8 * channel, ByteManipulationTools.LITTLEENDIAN);
			peaks[channel][1] = ByteManipulationTools.getDecimalValueUnsigned(getData(), 8 + 8 * channel + 4, 4, ByteManipulationTools.LITTLEENDIAN);
			info += "Channel " + channel + " peak: " + NumberManipulationTools.setDecimalPlaces(peaks[channel][0], 3) + ", at sample " + peaks[channel][1] + "<br/>";
		}//End loop
		this.setInfo(info);
	}//End setInfo
	
	public double getVersion() {
		return version;
	}
	public double getTimeStamp() {
		return timeStamp;
	}
	public double[][] getPeaks() {
		return peaks;
	}
}
//https://web.archive.org/web/20081201144551/http://music.calarts.edu/~tre/PeakChunk.html