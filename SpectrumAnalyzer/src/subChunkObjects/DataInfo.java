package subChunkObjects;

import interfaces.Info;

public class DataInfo implements Info {
	/**
	 * Sound data stored in bytes
	 */
	byte[] data;
	
	/**
	 * Parsed data into channel values
	 */
	private double[][] channelSeparatedData;
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public double[][] getChannelSeparatedData() {
		return channelSeparatedData;
	}

	public void setChannelSeparatedData(double[][] channelSeparatedData) {
		this.channelSeparatedData = channelSeparatedData;
	}


	@Override
	public String toString() {
		return "";
	}//End toString
}
