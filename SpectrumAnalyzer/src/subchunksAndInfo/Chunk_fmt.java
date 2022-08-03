package subchunksAndInfo;

import tools.ByteManipulationTools;

public class Chunk_fmt extends SubChunks {

	private int format;
	private int dataFormat;
	private int bitsPerSample;
	private int sampleRate;
	private int bitRate;
	private int channels;
	private int blockAlign;

	private String info;
	private String[] formatInfo;//first place in the array is the format and the subsequent information is relevant to the format
	

	public Chunk_fmt(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
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
		info += "AudioFormat: ";
		info += formatFinder(format);
		
		//Gets the number of channels
		info += "<br/>";
		channels = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 2, 2);
		info += "Number of channels: " + channels;

		//Block align (Bytes for all samples)
		info += "<br/>";
		blockAlign = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 12, 2);
		info += "Bytes per block: " + blockAlign + " bytes";

		//Bits per sample
		info += "<br/>";
		bitsPerSample = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 14, 2);
		info += "Bits per sample: " + bitsPerSample + " bits";
		
		//Gets the sample rate
		info += "<br/>";
		sampleRate = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 4, 4);
		info += "Sample rate: " + (sampleRate / 10)/100.0 + " kHz";
		
		//Gets the bit rate
		info += "<br/>";
		bitRate = ByteManipulationTools.getLittleEndianValueUnsigned(temp, 8, 4) * 8;
		info += "Bit rate: " + (bitRate / 10)/100.0 + " kb/s";
		
		//Extra information for none integer PCM formats
		if (format != 1 && this.getSubChunkSize() > 16) {
			info += "<br/>";
			formatInfo = formatHandling(temp, info);
			info += formatInfo[0];
			formatInfo[0] = format + "";
		}
		
		this.setInfo(info);
	}
	/**
	 * Handles extra information added by different formats
	 */
	public String[] formatHandling(int[] temp, String info) {
		String[] allInfo = null;
		String extraInfo = "";
		extraInfo += "<br/>";
		if (format == 2) {
			allInfo = new String[1];
		}
		else if (format == 3) {
			allInfo = new String[1];
		}
		else if (format == 6) {
			allInfo = new String[1];
		}
		else if (format == 7) {
			allInfo = new String[1];
		}
		else if (format == 65534) {
			allInfo = new String[4];
			/*String[] speakersInfo = {"Front left", "Front right", "Front center", "Low frequency", "Back left", "Back right",
					"Front left of center", "Front right of center", "Back center", "Side left", "Side right", "Top center",
					"Top front left", "Top front center", "Top front right", "Top back left", "Top back center", "Top back right"};*/
			String[] speakersInfo = {"FL", "FR", "FC", "LowFreq", "BL", "BR",
					"FLofCen", "FRofCen", "BC", "SL", "SR", "TC",
					"TFL", "TFC", "TFR", "TBL", "TBC", "TBR"};
			
			//Valid bits
			allInfo[1] = "" + ByteManipulationTools.getLittleEndianValueUnsigned(temp, 18, 2);
			extraInfo += "Valid bits per sample: " + allInfo[1];
			
			//Channels layout
			extraInfo += "<br/>";
			allInfo[2] = "" + ByteManipulationTools.getLittleEndianValueUnsigned(temp, 20, 4);
			extraInfo += "Channels layout: ";
			byte[] bits = ByteManipulationTools.decimalToBits(Integer.parseInt(allInfo[2]));
			int assignedChannels = 0;
			String channelsLocation = "";
			//Assigns channels to speakers
			for (int i = 0; i < bits.length; i++) {
				if (assignedChannels < channels) {
					if (bits[bits.length - 1 - i] != 0) {
						assignedChannels++;
						/*channelsLocation += "Channel " + (i+1) + " = " + speakersInfo[i] + "; ";*/
						channelsLocation += speakersInfo[i] + " ";
						
					}
				}
				else break;
			}
			if (assignedChannels != 0) {
				allInfo[2] = channelsLocation;
				extraInfo += allInfo[2].substring(0, allInfo[2].length() - 1);
			}
			
			//GUID
			extraInfo += "<br/>";
			allInfo[3] = "" + ByteManipulationTools.getLittleEndianValueUnsigned(temp, 24, 2);
			dataFormat = Integer.parseInt(allInfo[3]);
			extraInfo += "Sub format GUID: " + formatFinder(Integer.parseInt(allInfo[3]));
		}
		else return null;
		allInfo[0] = "<br/><i>Format specific information:</i>" + extraInfo;
		return allInfo;
		
	}
	/**
	 * Returns the format corresponding to the integer
	 * @param format the format stored as an integer
	 * @return The actual format
	 */
	public String formatFinder(int f) {
		String format;
		if (f == 1) format = f + " -> integer PCM";
		else if (f == 2) format = f + " -> ADPCM";
		else if (f == 3) format = f + " -> floating point PCM";
		else if (f == 6) format = f + " -> A-law";
		else if (f == 7) format = f + " -> µ-law";
		else if (f == 80) format = f + " -> MPEG";
		else if (f == 65534) format = f + " -> WAVE_FORMAT_EXTENSIBLE";
		else format = f + " -> UNKNOWN FORMAT";
		return format;		
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
	public String[] getFormatInfo() {
		return formatInfo;
	}
	public int getChannels() {
		return channels;
	}
	public int getBlockAlign() {
		return blockAlign;
	}

	@Override
	public String toString() {
		return "<B>subchunk:</B> " + this.getSubChunkName() + "<br/>" + this.getInfo();
	}

}
