package subchunkObjects;

import interfaces.Info;
import tools.ValueParsingTools;

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
	private int nbChannels;
	/**
	 * Number of bytes used per block (One sample for each channel)
	 */
	private int blockAlign;
	/**
	 * Variable containing the successive abbreviated channel names
	 */
	private String[] channelsLocation;
	/**
	 * Variable containing the successive non-abbreviated channel names
	 */
	private String[] channelsLocationLongName;
	
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

	public int getNbChannels() {
		return nbChannels;
	}

	public void setNbChannels(int nbChannels) {
		this.nbChannels = nbChannels;
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

	public String[] getChannelsLocation() {
		return channelsLocation;
	}

	public void setChannelsLocation(String[] channelsLocation) {
		this.channelsLocation = channelsLocation;
	}

	public String[] getChannelsLocationLongName() {
		return channelsLocationLongName;
	}

	public void setChannelsLocationLongName(String[] channelsLocationLongName) {
		this.channelsLocationLongName = channelsLocationLongName;
	}

	@Override
	public String toString() {
		String formatString = "<B>Audio file specifications:</B><br/>";
		formatString += "Format: " + stringFormat + "<br/>";
		formatString += "Bits per sample: " + validBitsPerSample + "<br/>";
		formatString += "Sample rate: " + ValueParsingTools.refinedMetrics(sampleRate) + "Hz<br/>";
		formatString += "Number of channels: " + nbChannels + " channels<br/>";
		formatString += "Bit rate: " + ValueParsingTools.refinedMetrics(bitRate) + "b/sec<br/>";
		formatString += "Channel mapping: ";
		for (int channel = 0; channel < channelsLocation.length; channel++) {
			formatString += channelsLocation[channel] + " ";
		}
		return formatString;
	}//End toString
}
