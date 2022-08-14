package subchunksAndInfo;

import tools.ByteManipulationTools;

public class Chunk_PEAK extends SubChunks {
	private double version;
	private double timeStamp;
	
	private double[][] peaks;//[nbChannels][PeakValue]
	
	public Chunk_PEAK(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		version = ByteManipulationTools.getDecimalValueUnsigned(data, 0, 4, ByteManipulationTools.LITTLEENDIAN);
		timeStamp = ByteManipulationTools.getDecimalValueUnsigned(data, 4, 4, ByteManipulationTools.LITTLEENDIAN);
		int nbChannels = (data.length - 8) / 8;
		peaks = new double[nbChannels][2];
		for (int channel = 0; channel < nbChannels; channel++) {
			peaks[channel][0] = ByteManipulationTools.getFloatingP32(data, 8 + 8 * channel, ByteManipulationTools.LITTLEENDIAN);
			System.out.println(peaks[channel][0]);
			peaks[channel][1] = ByteManipulationTools.getDecimalValueUnsigned(data, 8 + 8 * channel + 4, 4, ByteManipulationTools.LITTLEENDIAN);
		}//End loop
	}//End Chunk_PEAK
	
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
