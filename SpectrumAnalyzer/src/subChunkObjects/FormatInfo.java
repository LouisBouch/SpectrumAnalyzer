package subChunkObjects;

import interfaces.Info;

public class FormatInfo implements Info {
	/**
	 * Audio format
	 */
	private int format;
	/**
	 * Format as a string
	 */
	private String stringFormat;
	/**
	 * Amount of stored bits per sample
	 */
	private int bitsPerSample;
	/**
	 * Amount of bits used per sample
	 */
	private int validBitsPerSample;
	/**
	 * Number of samples taken every second
	 */
	private int sampleRate;
	/**
	 * Number of bits stored per second
	 */
	private int bitRate;
	/**
	 * Number of channels used
	 */
	private int channels;
	/**
	 * Number of bytes used per block (One sample for each channel)
	 */
	private int blockAlign;
	/**
	 * Variable containing the successive abbreviated channel names
	 */
	private String channelsLocation = "";
	/**
	 * Variable containing the successive non-abbreviated channel names
	 */
	private String channelsLocationLongName = "";
	
	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public int getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(int bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	public int getValidBitsPerSample() {
		return validBitsPerSample;
	}

	public void setValidBitsPerSample(int validBitsPerSample) {
		this.validBitsPerSample = validBitsPerSample;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getBitRate() {
		return bitRate;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public int getBlockAlign() {
		return blockAlign;
	}

	public void setBlockAlign(int blockAlign) {
		this.blockAlign = blockAlign;
	}
	
	public String getStringFormat() {
		return stringFormat;
	}

	public void setStringFormat(String stringFormat) {
		this.stringFormat = stringFormat;
	}

	public String getChannelsLocation() {
		return channelsLocation;
	}

	public void setChannelsLocation(String channelsLocation) {
		this.channelsLocation = channelsLocation;
	}

	public String getChannelsLocationLongName() {
		return channelsLocationLongName;
	}

	public void setChannelsLocationLongName(String channelsLocationLongName) {
		this.channelsLocationLongName = channelsLocationLongName;
	}

	@Override
	public String toString() {
		return "format";
	}//End toString
}
