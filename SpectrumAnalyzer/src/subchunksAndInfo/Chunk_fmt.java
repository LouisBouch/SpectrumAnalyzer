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

	private String info;
	/**Additional information depending on the format used;<br/>
	 * 65534:<br/>
	 * 0 -> valid bits per sample<br/>
	 * 1 -> channel mask<br/>
	 * 2 -> actual format<br/>
	 * 
	 * 
	 */
//	private String[] formatInfo = null;
	
	
	private HashMap<Integer, String> formats = new HashMap<Integer, String>();
	

	public Chunk_fmt(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		formatInitializer();
		setInfo();
	}
	
	/**
	 * Sets the info for the current subchunk
	 */
	public void setInfo() {
		info = "";
		int[] temp = new int[this.getSubChunkSize()];
		for (int i = 0; i < this.getSubChunkSize(); i++) {
			if (this.getData()[i] < 0) temp[i] = ByteManipulationTools.unsignedVersionOfByteTwosComplement(this.getData()[i]);
			else temp[i] = this.getData()[i];
		}
		
		format = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 0, 2);
		dataFormat = format;
		info += "AudioFormat: " + format + " -> " + formatFinder(format);
		
		//Gets the number of channels
		channels = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 2, 2);
		info += "<br/>Number of channels: " + channels;

		//Block align (Bytes for all samples)
		blockAlign = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 12, 2);
		info += "<br/>Bytes per block: " + blockAlign + " bytes";

		//Bits per sample
		bitsPerSample = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 14, 2);
		validBitsPerSample = bitsPerSample;
		info += "<br/>Bits per sample: " + bitsPerSample + " bits";
		
		//Gets the sample rate
		sampleRate = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 4, 4);
		info += "<br/>Sample rate: " + (sampleRate / 10)/100.0 + " kHz";
		
		//Gets the bit rate
		bitRate = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 8, 4) * 8;
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
//			formatInfo = new String[1];
		}
		else if (format == 3) {
//			formatInfo = new String[1];
		}
		else if (format == 6) {
//			formatInfo = new String[1];
		}
		else if (format == 7) {
//			formatInfo = new String[1];
		}
		else if (format == 65534) {
//			formatInfo = new String[4];
			String[] speakersInfoLongName = {"Front left", "Front right", "Front center", "Low frequency", "Back left", "Back right",
					"Front left of center", "Front right of center", "Back center", "Side left", "Side right", "Top center",
					"Top front left", "Top front center", "Top front right", "Top back left", "Top back center", "Top back right"};
			String[] speakersInfo = {"FL", "FR", "FC", "LowFreq", "BL", "BR",
					"FLofCen", "FRofCen", "BC", "SL", "SR", "TC",
					"TFL", "TFC", "TFR", "TBL", "TBC", "TBR"};
			
			//Valid bits
			validBitsPerSample = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 18, 2);
			info += "<br/>Valid bits per sample: " + validBitsPerSample;
			
			//Channels layout
			String channelsByteValue = "" + ByteManipulationTools.getLittleEndianValueUnsigned(temp, 20, 4);
			byte[] bits = ByteManipulationTools.decimalToBits(Integer.parseInt(channelsByteValue));
			//Assigns channels to speakers
			int assignedChannel;
			for (assignedChannel = 0; assignedChannel < bits.length; assignedChannel++) {
				if (assignedChannel < channels) {
					if (bits[bits.length - 1 - assignedChannel] != 0) {
						/*channelsLocation += "Channel " + (i+1) + " = " + speakersInfo[i] + "; ";*/
						channelsLocation += speakersInfo[assignedChannel] + " ";
						channelsLocationLongName += speakersInfoLongName[assignedChannel] + ".";
						
					}
				}
				else break;
			}
			if (assignedChannel != 0) {
				info += "<br/>Channels layout: " + channelsLocation.substring(0, channelsLocation.length() - 1);
			}
			
			//GUID
			dataFormat = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 24, 2);
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
