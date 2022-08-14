package subchunksAndInfo;

import java.util.HashMap;

import tools.ByteManipulationTools;

public class Chunk_fmt extends SubChunks {

	private int format;
	private int dataFormat;
	private int bitsPerSample;
	private int validBitsPerSample;
	private int sampleRate;
	private int bitRate;
	private int channels;
	private int blockAlign;
	
	/**
	 * Variable containing the successive abbreviated channel names
	 */
	private String channelsLocation = "";
	/**
	 * Variable containing the successive non-abbreviated channel names
	 */
	private String channelsLocationLongName = "";
	/**
	 * Information about the wav file in a single string
	 */
	private String info = "";
	
	
	private HashMap<Integer, String> formats = new HashMap<Integer, String>();
	

	public Chunk_fmt(String subChunkName, int subChunkSize, byte[] data, WavReader infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		formatInitializer();
		setInfo();
	}
	
	/**
	 * Sets the info for the current subchunk
	 */
	public void setInfo() {
		int[] temp = new int[this.getSubChunkSize()];
		for (int i = 0; i < this.getSubChunkSize(); i++) {
			if (this.getData()[i] < 0) temp[i] = (int) ByteManipulationTools.unsignedVersionOfByteTwosComplement(this.getData()[i]);
			else temp[i] = this.getData()[i];
		}
		
		format = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 0, 2, ByteManipulationTools.LITTLEENDIAN);
		dataFormat = format;
		info += "AudioFormat: " + format + " -> " + formatFinder(format);
		
		//Gets the number of channels
		channels = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 2, 2, ByteManipulationTools.LITTLEENDIAN);
		info += "<br/>Number of channels: " + channels;

		//Block align (Bytes for all samples)
		blockAlign = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 12, 2, ByteManipulationTools.LITTLEENDIAN);
		info += "<br/>Bytes per block: " + blockAlign + " bytes";

		//Bits per sample
		bitsPerSample = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 14, 2, ByteManipulationTools.LITTLEENDIAN);
		validBitsPerSample = bitsPerSample;
		info += "<br/>Bits per sample: " + bitsPerSample + " bits";
		
		//Gets the sample rate
		sampleRate = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 4, 4, ByteManipulationTools.LITTLEENDIAN);
		info += "<br/>Sample rate: " + (sampleRate / 10)/100.0 + " kHz";
		
		//Gets the bit rate
		bitRate = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 8, 4, ByteManipulationTools.LITTLEENDIAN) * 8;
		info += "<br/>Bit rate: " + (bitRate / 10)/100.0 + " kb/s";
		
		//Extra information for none integer PCM formats
		if (format != 1 && this.getSubChunkSize() > 16) {
			formatHandling(temp);
		}
		//Assigns the channels if they haven't been assigned
		if (channelsLocationLongName == "" && channelsLocation == "") {
			if (channels == 1) {
				channelsLocationLongName = "Left and right.";
				channelsLocation = "LR";
			}
			if (channels == 2) {
				channelsLocationLongName = "Left.Right.";
				channelsLocation = "FL FR";
			}
		}
		this.setInfo(info);
	}
	/**
	 * Handles extra information added by different formats
	 */
	public void formatHandling(int[] temp) {
		info += "<br/><br/><i>Format specific information:</i>";
		
		if (format == 2) {
		}
		else if (format == 3) {
		}
		else if (format == 6) {
		}
		else if (format == 7) {
		}
		else if (format == 65534) {
			String[] speakersInfoLongName = {"Front left", "Front right", "Front center", "Low frequency", "Back left", "Back right",
					"Front left of center", "Front right of center", "Back center", "Side left", "Side right", "Top center",
					"Top front left", "Top front center", "Top front right", "Top back left", "Top back center", "Top back right"};
			String[] speakersInfo = {"FL", "FR", "FC", "LowFreq", "BL", "BR",
					"FLofCen", "FRofCen", "BC", "SL", "SR", "TC",
					"TFL", "TFC", "TFR", "TBL", "TBC", "TBR"};
			
			//Valid bits
			validBitsPerSample = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 18, 2, ByteManipulationTools.LITTLEENDIAN);
			info += "<br/>Valid bits per sample: " + validBitsPerSample;
			
			//Channels layout
			String channelsByteValue = "" + (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 20, 4, ByteManipulationTools.LITTLEENDIAN);
			byte[] bits = ByteManipulationTools.decimalToBits(Integer.parseInt(channelsByteValue));
			//Assigns channels to speakers
			int assignedChannels = 0;
			for (int channel = 0; channel < bits.length; channel++) {
				if (assignedChannels < channels) {
					if (bits[bits.length - 1 - channel] != 0) {
						/*channelsLocation += "Channel " + (i+1) + " = " + speakersInfo[i] + "; ";*/
						channelsLocation += speakersInfo[channel] + " ";
						channelsLocationLongName += speakersInfoLongName[channel] + ".";
						assignedChannels++;
					}
				}
				else break;
			}
			if (assignedChannels != 0) {
				info += "<br/>Channels layout: " + channelsLocation.substring(0, channelsLocation.length() - 1);
			}
			
			//GUID
			dataFormat = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 24, 2, ByteManipulationTools.LITTLEENDIAN);
			info += "<br/>Sub format GUID: " + dataFormat + "  -> " + formatFinder(dataFormat);
		}
		else return;
//		formatInfo[0] = "<br/><i>Format specific information:</i>" + extraInfo;
	}
	/**
	 * Returns the format corresponding to the integer
	 * @param format the format stored as an integer
	 * @return The actual format
	 */
	public String formatFinder(int f) {
		String format = formats.get(f);
		return format == null ? "UNKNOWN FORMAT" : format;	
	}
	/**
	 * Initializes the hashmap containing the different formats
	 */
	public void formatInitializer() {
		formats.put(1, "integer PCM");
		formats.put(2, "ADPCM");
		formats.put(3, "floating point PCM");
		formats.put(6, "A-law");
		formats.put(7, "µ-law");
		formats.put(80, "MPEG");
		formats.put(65534, "WAVE_FORMAT_EXTENSIBLE");
	}
	//Getters for the different properties of the fmt subchunk
	public int getFormat() {
		return format;
	}
	public int getDataFormat() {
		return dataFormat;
	}
	public int getBitsPerSample() {
		return bitsPerSample;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public int getBitRate() {
		return bitRate;
	}
	public int getChannels() {
		return channels;
	}
	public int getBlockAlign() {
		return blockAlign;
	}
	public int getValidBitsPerSample() {
		return validBitsPerSample;
	}
	public String getChannelsLocation() {
		return channelsLocation;
	}
	public String getChannelsLocationLongName() {
		return channelsLocationLongName;
	}
	
//	/**
//	 * Returns additional format info
//	 * @return Additional information depending on the format used;<br/>
//	 * format 65534:<br/>
//	 * 0 -> valid bits per samples<br/>
//	 * 1 -> channel mask<br/>
//	 * 2 -> channel mask long names<br/>
//	 * 3 -> actual format<br/>
//	 */
//	public String[] getFormatInfo() {
//		return formatInfo;
//	}

	@Override
	public String toString() {
		return "<B>subchunk:</B> " + this.getSubChunkName() + "<br/>" + this.getInfo();
	}

}
