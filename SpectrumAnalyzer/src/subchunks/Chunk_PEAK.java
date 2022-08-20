package subchunks;

import subchunkObjects.PeakInfo;
import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_PEAK extends SubChunks {
//	private String info;
	
	private double version;
	private double timeStamp;
	
	private double[][] peaks;//[nbChannels][PeakValue]
	
	private PeakInfo peakInfo = new PeakInfo();
	
	public Chunk_PEAK(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
	}//End Chunk_PEAK
	/**
	 * Sets the necessary information
	 */
	@Override
	public void setInfo() {
//		info = "";
		version = ByteManipulationTools.getDecimalValueUnsigned(getData(), 0, 4, ByteManipulationTools.LITTLEENDIAN);
		timeStamp = ByteManipulationTools.getDecimalValueUnsigned(getData(), 4, 4, ByteManipulationTools.LITTLEENDIAN);
		int nbChannels = this.getInfoReservoir().getFormatInfo().getNbChannels();
		peaks = new double[nbChannels][2];
		for (int channel = 0; channel < nbChannels; channel++) {
			peaks[channel][0] = ByteManipulationTools.getFloatingP32(getData(), 8 + 8 * channel, ByteManipulationTools.LITTLEENDIAN);
			peaks[channel][1] = ByteManipulationTools.getDecimalValueUnsigned(getData(), 8 + 8 * channel + 4, 4, ByteManipulationTools.LITTLEENDIAN);
			peakInfo.addPeak(peaks[channel][0], (int) peaks[channel][1], this.getInfoReservoir().getFormatInfo().getChannelsLocationLongName()[channel], peaks[channel][1] / this.getInfoReservoir().getFormatInfo().getSampleRate());
//			info += "Channel " + (channel + 1) + " peak: " + NumberManipulationTools.setDecimalPlaces(peaks[channel][0], 3) + ", at sample " + peaks[channel][1] + "<br/>";
		}//End loop
//		this.setInfo(info);
		this.getInfoReservoir().setPeakInfo(peakInfo);
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