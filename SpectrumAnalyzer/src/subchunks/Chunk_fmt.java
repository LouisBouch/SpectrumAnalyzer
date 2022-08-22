package subchunks;

import java.util.HashMap;

import subchunkObjects.FormatInfo;
import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_fmt extends SubChunks {

	private FormatInfo formatInfo = new FormatInfo();
	
	private HashMap<Integer, String> formats = new HashMap<Integer, String>();

	public Chunk_fmt(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		formatInitializer();
	}
	
	/**
	 * Sets the info for the current subchunk
	 */
	@Override
	public void setInfo() {
		int[] temp = new int[this.getSubChunkSize()];
		for (int i = 0; i < this.getSubChunkSize(); i++) {
			if (this.getData()[i] < 0) temp[i] = (int) ByteManipulationTools.unsignedVersionOfByteTwosComplement(this.getData()[i]);
			else temp[i] = this.getData()[i];
		}
		
		int format = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 0, 2, ByteManipulationTools.LITTLEENDIAN);
		formatInfo.setFormat(format);
		formatInfo.setStringFormat(formatFinder(format));
		
		//Gets the number of channels
		int channels = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 2, 2, ByteManipulationTools.LITTLEENDIAN);
		formatInfo.setNbChannels(channels);

		//Block align (Bytes for all samples)
		int blockAlign = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 12, 2, ByteManipulationTools.LITTLEENDIAN);
		formatInfo.setBlockAlign(blockAlign);

		//Bits per sample
		int bitsPerSample = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 14, 2, ByteManipulationTools.LITTLEENDIAN);
		formatInfo.setBitsPerSample(bitsPerSample);
		formatInfo.setValidBitsPerSample(bitsPerSample);
		
		//Gets the sample rate
		int sampleRate = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 4, 4, ByteManipulationTools.LITTLEENDIAN);
		formatInfo.setSampleRate(sampleRate);
		
		//Gets the bit rate
		int bitRate = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 8, 4, ByteManipulationTools.LITTLEENDIAN) * 8;
		formatInfo.setBitRate(bitRate);
		
		//Extra information for none integer PCM formats
		if (format != 1 && this.getSubChunkSize() > 16) {
			formatHandling(temp);
		}
		//Assigns the channels if they haven't been assigned
		if (formatInfo.getChannelsLocationLongName() == null && formatInfo.getChannelsLocation() == null) {
			String[] channelsLocation;
			String[] channelsLocationLongName;
			if (channels == 1) {
				channelsLocation = new String[1];
				channelsLocation[0] = "LR";
				channelsLocationLongName = new String[1];
				channelsLocationLongName[0] = "Left and right";
				formatInfo.setChannelsLocation(channelsLocation);
				formatInfo.setChannelsLocationLongName(channelsLocationLongName);
			}
			if (channels == 2) {
				channelsLocation = new String[2];
				channelsLocation[0] = "FL";
				channelsLocation[1] = "FR";
				channelsLocationLongName = new String[2];
				channelsLocationLongName[0] = "Left";
				channelsLocationLongName[1] = "Right";
				formatInfo.setChannelsLocation(channelsLocation);
				formatInfo.setChannelsLocationLongName(channelsLocationLongName);
			}
		}
//		this.setInfo(info);
		this.getInfoReservoir().setFormatInfo(formatInfo);
	}
	/**
	 * Handles extra information added by different formats
	 */
	public void formatHandling(int[] temp) {
//		info += "<br/><br/><i>Format specific information:</i>";
		int format = formatInfo.getFormat();
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
			int validBitsPerSample = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 18, 2, ByteManipulationTools.LITTLEENDIAN);
//			info += "<br/>Valid bits per sample: " + validBitsPerSample;
			formatInfo.setValidBitsPerSample(validBitsPerSample);
			
			//Channels layout
			String channelsByteValue = "" + (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 20, 4, ByteManipulationTools.LITTLEENDIAN);
			byte[] bits = ByteManipulationTools.decimalToBits(Integer.parseInt(channelsByteValue), ByteManipulationTools.UNSIGNED);
			//Assigns channels to speakers
			int assignedChannels = 0;
			//Creates string array with N/A inside
			String[] NAString = new String[formatInfo.getNbChannels()];
			for (int index = 0; index < formatInfo.getNbChannels(); index++) {
				NAString[index] = "N/A";
			}
			//Sets the real arrays-
			String[] channelsLocationLongName = NAString.clone();
			String[] channelsLocation = NAString.clone();
			channelsLocation = NAString;
			for (int channel = 0; channel < bits.length; channel++) {
				if (assignedChannels < formatInfo.getNbChannels()) {
					if (bits[bits.length - 1 - channel] != 0) {
						channelsLocation[assignedChannels] = speakersInfo[channel];
						channelsLocationLongName[assignedChannels] = speakersInfoLongName[channel];
						assignedChannels++;
					}
				}
				else break;
			}
			if (assignedChannels != 0) {
				formatInfo.setChannelsLocation(channelsLocation);
				formatInfo.setChannelsLocationLongName(channelsLocationLongName);
			}
			
			//GUID
			int dataFormat = (int) ByteManipulationTools.getDecimalValueUnsigned(temp, 24, 2, ByteManipulationTools.LITTLEENDIAN);
			formatInfo.setFormat(dataFormat);
			formatInfo.setStringFormat(formatFinder(dataFormat));
		}
		else return;
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
}
//https://ccrma.stanford.edu/courses/422-winter-2014/projects/WaveFormat/#:~:text=A%20WAVE%20file%20is%20often,form%20the%20%22Canonical%20form%22.