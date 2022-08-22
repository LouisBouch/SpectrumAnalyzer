package wavParsingAndStoring;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import subchunkObjects.FormatInfo;
import subchunks.SubChunks;
import tools.ByteManipulationTools;

public class WavReader {
	private ArrayList<SubChunks> subChunks = new ArrayList<>();
	private byte[] binInfo;
	
	private String fileName;
	
	private WavInfo infoReservoir;
	
	private final int MULAW = 0;
	private final int ALAW = 1;
	
	private double tIni;
	private double tFin;
	
	//Constructor
	public WavReader(byte[] binInfo, String fileName) {
		infoReservoir = new WavInfo(binInfo, fileName);
		this.binInfo = binInfo;
		this.fileName = fileName;
		getInfo();
		try {
//			Chunk_fmt fmt = (Chunk_fmt) subChunks.get("subchunksAndInfo.Chunk_fmt");
//			int fm = fmt.getDataFormat();
//			int channels = fmt.getChannels();
			FormatInfo formatInfo = infoReservoir.getFormatInfo();
			int channels = formatInfo.getNbChannels();
//			int bitsPerSample = fmt.getBitsPerSample();;
			int bitsPerSample = formatInfo.getBitsPerSample();;
			int validBitsPerSample = bitsPerSample;
			
			if (formatInfo.getFormat() == 65534) {
//				validBitsPerSample = fmt.getValidBitsPerSample();
				validBitsPerSample = formatInfo.getValidBitsPerSample();
			}
			
//			Chunk_data data = (Chunk_data) subChunks.get("subchunksAndInfo.Chunk_data");
//			byte[] rawData = data.getData();
			byte[] rawData = infoReservoir.getDataInfo().getData();
			
			tIni = System.nanoTime();
			double[][] channelSeparatedData = handlingRawData(formatInfo.getFormat(), validBitsPerSample, bitsPerSample, channels, rawData);
			tFin = System.nanoTime();
			System.out.println((tFin - tIni) / 1E6);
			infoReservoir.getDataInfo().setChannelSeparatedData(channelSeparatedData);
			
		}
		catch (ClassCastException e) {
			System.out.println(e);
		}
	}//End WavInfo
	
	/**
	 * Reads information about the file and returns it as a string
	 */
	public void getInfo() {
		String subChunkname;
		
		int fileSize = binInfo.length;
		int offset = 12;
		
		byte[] subChunkData;
		boolean paddingByte;
		
		int subChunkSize;
		
//		//Sets the divisor and suffix for the filesize
//		String suffix = "b";
//		int divisor = 1;
//		
//		if (fileSize > 10000000) {
//			suffix = "Mb";
//			divisor = 1000000;
//		}
//		else if (fileSize > 10000) {
//			suffix = "Kb";
//			divisor = 1000;
//		}

		double[] temp = new double[8];
		boolean foundFmt = false;//Goes to true once the fmt subchunk has been found
		//Reads all the subchunks and places them into the subChunk array
		while (offset < fileSize) {
			for (int i = 0; i < 8; i++) {
				if (binInfo[offset + i] < 0) temp[i] = ByteManipulationTools.unsignedVersionOfByteTwosComplement(binInfo[offset + i]);
				else temp[i] = binInfo[offset + i];
			}

			subChunkname = ("" + (char)temp[0] + (char)temp[1] + (char)temp[2] + (char)temp[3]);
			subChunkSize = (int) (temp[4] + temp[5] * 256 + temp[6] * 256*256 +  temp[7] * 256*256*256);//Little endian (base 256)
			if ((!foundFmt && subChunkname.equals("fmt ")) || (foundFmt && !subChunkname.equals("fmt "))) {//Makes sure the fmt is the first chunk to be loaded
				//Uneven chunk sizes cause problems. This can be fixed by adding one byte
				paddingByte = false;
				if (subChunkSize % 2 != 0) {
					subChunkSize++;
					paddingByte = true;
				}
				subChunkData = new byte[subChunkSize];

				//Fills the subChunkData field, which contains the data of the subchunk
				for (int i = 0; i < subChunkSize; i++) {
					subChunkData[i] = binInfo[offset + 8 + i];
				}
				try {
					Class<?> subChunk = Class.forName("subchunks.Chunk_" + subChunkname.replaceAll("\\s", ""));
					Constructor<?> constructor = subChunk.getConstructor(String.class, int.class, byte[].class, WavInfo.class, boolean.class);
					Object sub = constructor.newInstance(subChunkname, 
							subChunkSize, 
							subChunkData,
							infoReservoir, paddingByte);
					if (sub instanceof SubChunks) subChunks.add((SubChunks) sub);
//					subChunks.put(subChunk.getName(), (SubChunks) sub);
				}

				catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassCastException e) {
					System.out.println(e + ", cause: " + e.getCause());
//					e.printStackTrace();
//					subChunks.put("subChunk" + subChunks.size(), new SubChunks(subChunkname, subChunkSize, subChunkData, infoReservoir, paddingByte));

				}

			}
			offset = offset + subChunkSize + 8;//+8 because the first 8 bytes are not taken into account
			if (!foundFmt && subChunkname.equals("fmt ")) {
				offset = 12;
				foundFmt = true;
			}
		}
		//Sets the information for all the subchunks
		for (int subC = 0; subC < subChunks.size(); subC++) {
			subChunks.get(subC).setInfo();
		}
		//Compresses the information of the subchunks into a single string
		double time = findTime();
		infoReservoir.setTime(time);
//		Set<String> keys = subChunks.keySet();
//		fileInfo = "<html>" + "<B>Name:</B> " + fileName + "<br/><B>Weight:</B> " + fileSize/divisor + " " + suffix + "<br/><B>Data duration:</B> " + TimeTools.refinedTime(time) + "<br/><br/><br/>";
//		for (String key : keys) {
//			fileInfo += subChunks.get(key).toString() + "<br/><br/>";
//		}
//		fileInfo += "<html>";
	}
	/**
	 * Uses the raw data to create the waveform
	 * @param format The format of the wav file
	 * @param validBitsPerSample The amount of bits containing data for each sample
	 * @param bitsPerSample Container size for the bits
	 * @param rawData The raw data of the wav file
	 * @param nbChannels The number of channels
	 */
	public double[][] handlingRawData(int format, int validBitsPerSample, int bitsPerSample, int nbChannels, byte[] rawData) {
		int storedBytesPerSample = (int) Math.ceil(bitsPerSample / 8.0);
		if (format == 1) return getPCMData(storedBytesPerSample, validBitsPerSample, bitsPerSample, nbChannels, rawData);
		if (format == 3) return getPCMFloatData(storedBytesPerSample, validBitsPerSample, bitsPerSample, nbChannels, rawData);
		if (format == 6) return getDecodedLaw(nbChannels, rawData, ALAW);
		if (format == 7) return getDecodedLaw(nbChannels, rawData, MULAW);
			
		
		return null;
	}
	/**
	 * Uses the raw data to create the waveform with PCM encoding
	 * @param storedBytesPerSample The amount of bytes per sample
	 * @param validBitsPerSample The amount of bits containing data for each sample
	 * @param bitsPerSample Container size for the bits
	 * @param rawData The raw data of the wav file
	 * @param nbChannels The number of channels
	 * @return The data separated into channels
	 */
	public double[][] getPCMData(int storedBytesPerSample, int validBitsPerSample, int bitsPerSample, int nbChannels, byte[] rawData) {
		int bytesPerChannels = rawData.length/nbChannels;//Number of bytes for a given channel
		int samplesPerChannel = bytesPerChannels / storedBytesPerSample;//Total amount of samples / number of channels
		int sampleByteOffset = nbChannels*storedBytesPerSample;//Offset of bytes between different samples
		int initialOffsetPerChannel; //Each channel has an offset
		int eightBitOffset = 128;//With 8 bits, the mid value is 128 instead of 0. This requires an offset to be added
		double ratio = Math.pow(2, Math.ceil(bitsPerSample / 8.0) * 8 - 1);
		
		double[][] channelSeparatedData = new double[nbChannels][samplesPerChannel];
		//Separates the data from the different channels
		for (int channel = 0; channel < nbChannels; channel++) {
			initialOffsetPerChannel = channel * storedBytesPerSample;
			for (int sample = 0; sample < samplesPerChannel; sample++) {
				//8 bits samples are unsigned
				if (validBitsPerSample > 8) channelSeparatedData[channel][sample] = ByteManipulationTools.getDecimalValueSigned(rawData, initialOffsetPerChannel + sampleByteOffset * sample, storedBytesPerSample, ByteManipulationTools.LITTLEENDIAN);
				else channelSeparatedData[channel][sample] = ByteManipulationTools.getDecimalValueUnsigned(rawData, initialOffsetPerChannel + sampleByteOffset * sample, storedBytesPerSample, ByteManipulationTools.LITTLEENDIAN) - eightBitOffset;
				channelSeparatedData[channel][sample] /= ratio;
			}
		}
		return channelSeparatedData;
	}
	/**
	 * Uses the raw data to create the waveform with PCM float encoding
	 * @param storedBytesPerSample The amount of bytes per sample
	 * @param validBitsPerSample The amount of bits containing data for each sample
	 * @param bitsPerSample Container size for the bits
	 * @param rawData The raw data of the wav file
	 * @param nbChannels The number of channels
	 * @return The data separated into channels
	 */
	public double[][] getPCMFloatData(int storedBytesPerSample, int validBitsPerSample, int bitsPerSample, int nbChannels, byte[] rawData) {
		if (storedBytesPerSample != 4) return new double[1][0];
		int bytesPerChannels = rawData.length/nbChannels;//Number of bytes for a given channel
		int samplesPerChannel = bytesPerChannels / storedBytesPerSample;//Total amount of samples / number of channels
		int sampleByteOffset = nbChannels*storedBytesPerSample;//Offset of bytes between different samples
		int initialOffsetPerChannel; //Each channel has an offset
		
		double[][] channelSeparatedData = new double[nbChannels][samplesPerChannel];
		//Separates the data from the different channels
		for (int channel = 0; channel < nbChannels; channel++) {
			initialOffsetPerChannel = channel * storedBytesPerSample;
			for (int sample = 0; sample < samplesPerChannel; sample++) {
//				channelSeparatedData[channel][sample] = ByteManipulationTools.getFloatingP32(rawData, initialOffsetPerChannel + sampleByteOffset * sample, ByteManipulationTools.LITTLEENDIAN);
				channelSeparatedData[channel][sample] = ByteManipulationTools.getFloatingP32((int)ByteManipulationTools.getDecimalValueSigned(rawData, initialOffsetPerChannel + sampleByteOffset * sample, 4, ByteManipulationTools.LITTLEENDIAN));
			}
		}
		return channelSeparatedData;
	}
	/**
	 * Uses the raw data to create the waveform with A-Law encoding
	 * @param nbChannels The number of channels
	 * @param rawData The raw data of the wav file
	 * @return The data separated into channels
	 */
	public double[][] getDecodedLaw(int nbChannels, byte[] rawData, int law) {
		int bytesPerChannels = rawData.length/nbChannels;//Number of bytes for a given channel
		int samplesPerChannel = bytesPerChannels;//1 byte per sample
		int sampleByteOffset = nbChannels;//Offset of bytes between different samples
		int initialOffsetPerChannel; //Each channel has an offset
		
		double[][] channelSeparatedData = new double[nbChannels][samplesPerChannel];
		//Separates the data from the different channels
		for (int channel = 0; channel < nbChannels; channel++) {
			initialOffsetPerChannel = channel;
			for (int sample = 0; sample < samplesPerChannel; sample++) {
				byte byteInfo = rawData[sample * sampleByteOffset + initialOffsetPerChannel];
				int data;
				if (law == ALAW) data = ByteManipulationTools.decodeALaw(byteInfo);
				else data = ByteManipulationTools.decodeULaw(byteInfo);
				channelSeparatedData[channel][sample] = data * 1.0 / Math.pow(2, 15);
			}
		}
		return channelSeparatedData;
	}
	/**
	 * Finds the time it takes to play the file
	 * @return Returns the play time
	 */
	public double findTime() {
		double nbBlocks;//Number of times a value is sampled into different channels
		int sampleRate;
		double time = 0;
		try {
			sampleRate = infoReservoir.getFormatInfo().getSampleRate();
			if (infoReservoir.getFactInfo() != null) {
				nbBlocks = infoReservoir.getFactInfo().getNbSampleFrames();
			}
			else {
				double totalBytes = infoReservoir.getWeight();
				int bytesPerBlock = infoReservoir.getFormatInfo().getBlockAlign();
				nbBlocks = totalBytes / bytesPerBlock;
			}
			time = (nbBlocks * 1.0) / sampleRate;

		}
		catch (ClassCastException e) {
			System.out.println(e);
		}
		return time;
	}
	/**
	 * Gets the file name
	 * @return The file name
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * Gets the information about the wav file
	 * @return The infoReservoir variable
	 */
	public WavInfo getInfoReservoir() {
		return infoReservoir;
	}
//	/**
//	 * Gets information about the Wav file
//	 * @return The information
//	 */
//	public String getFileInfo() {
//		return fileInfo;
//	}
//	/**
//	 * Gets data from each channels
//	 * @return The data
//	 */
//	public double[][] getChannelSeparatedData() {
//		return channelSeparatedData;
//	}
//	/**
//	 * Gets all the subchunks
//	 * @return The subchunks
//	 */
//	public LinkedHashMap<String, SubChunks> getSubChunks() {
//		return subChunks;
//	}
}
