package subchunksAndInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Set;

import tools.ByteManipulationTools;
import tools.TimeTools;

public class WavInfo {
	private LinkedHashMap<String, SubChunks> subChunks = new LinkedHashMap<String, SubChunks>();
	private byte[] binInfo;
	
	private String fileName;
	private String fileInfo;
	
	private double[][] channelSeparatedData;
	
	//Constructor
	public WavInfo(byte[] binInfo, String fileName) {
		this.binInfo = binInfo;
		this.fileName = fileName;
		getInfo();
		try {
			Chunk_fmt fmt = (Chunk_fmt) subChunks.get("subchunksAndInfo.Chunk_fmt");
			int fm = fmt.getDataFormat();
			int channels = fmt.getChannels();
			int bitsPerSample = fmt.getBitsPerSample();;
			int validBitsPerSample = bitsPerSample;
			
			if (fmt.getFormat() == 65534) {
				validBitsPerSample = fmt.getValidBitsPerSample();
			}
			
			Chunk_data data = (Chunk_data) subChunks.get("subchunksAndInfo.Chunk_data");
			byte[] rawData = data.getData();
			
			channelSeparatedData = handlingRawData(fm, validBitsPerSample, bitsPerSample, channels, rawData);
			
		}
		catch (ClassCastException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Reads information about the file and returns it as a string
	 */
	public void getInfo() {
		String subChunkname;
		
		int fileSize = binInfo.length;
		int offset = 12;
		
		byte[] subChunkData;
		boolean paddingByte;
		
		//Sets the divisor and suffix for the filesize
		String suffix = "b";
		int divisor = 1;
		int subChunkSize;
		if (fileSize > 10000000) {
			suffix = "Mb";
			divisor = 1000000;
		}
		else if (fileSize > 10000) {
			suffix = "Kb";
			divisor = 1000;
		}

		double[] temp = new double[8];
		
		//Reads all the subchunks and places them into the subChunk array
		while (offset < fileSize) {
			for (int i = 0; i < 8; i++) {
				if (binInfo[offset + i] < 0) temp[i] = ByteManipulationTools.unsignedVersionOfByteTwosComplement(binInfo[offset + i]);
				else temp[i] = binInfo[offset + i];
			}

			subChunkname = ("" + (char)temp[0] + (char)temp[1] + (char)temp[2] + (char)temp[3]);
			subChunkSize = (int) (temp[4] + temp[5] * 256 + temp[6] * 256*256 +  temp[7] * 256*256*256);//Little endian (base 256)
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
//				System.out.println("subchunks.Chunk_" + subChunkname.replaceAll("\\s", ""));
				Class<?> subChunk = Class.forName("subchunksAndInfo.Chunk_" + subChunkname.replaceAll("\\s", ""));
				Constructor<?> constructor = subChunk.getConstructor(String.class, int.class, byte[].class, WavInfo.class, boolean.class);
				Object sub = constructor.newInstance(subChunkname, 
						subChunkSize, 
						subChunkData,
						this, paddingByte);
				subChunks.put(subChunk.getName(), (SubChunks) sub);
			}
			
			catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassCastException e) {
				System.out.println(e + ", cause: " + e.getCause());
//				e.printStackTrace();
				subChunks.put("subChunk" + subChunks.size(), new SubChunks(subChunkname, subChunkSize, subChunkData, this, paddingByte));
				
			}
			
			offset = offset + subChunkSize + 8;//+8 because the first 8 bytes are not taken into account
		}
		
		//Compresses the information of the subchunks into a single string
		double time = findTime();
		Set<String> keys = subChunks.keySet();
		fileInfo = "<html>" + "<B>Name:</B> " + fileName + "<br/><B>Weight:</B> " + fileSize/divisor + " " + suffix + "<br/><B>Data duration:</B> " + TimeTools.refinedTime(time) + "<br/><br/><br/>";
		for (String key : keys) {
			fileInfo += subChunks.get(key).toString() + "<br/><br/>";
		}
		fileInfo += "<html>";
	}
	/**
	 * Uses the raw data to create the waveform
	 * @param format The format of the wav file
	 * @param validBitsPerSample The amount of bits containing data for each sample
	 * @param rawData The raw data of the wav file
	 * @param nbChannels The number of channels
	 */
	public double[][] handlingRawData(int format, int validBitsPerSample, int bitsPerSample, int nbChannels, byte[] rawData) {
		int storedBytesPerSample = (int) Math.ceil(bitsPerSample / 8.0);
		if (format == 1) {
			int bytesPerChannels = rawData.length/nbChannels;//Number of bytes for a given channel
			int samplesPerChannel = bytesPerChannels / storedBytesPerSample;//Total amount of samples / number of channels
			int sampleByteOffset = nbChannels*storedBytesPerSample;//Offset of bytes between different samples
			int initialOffsetPerChannel; //Each channel has an offset
			int eightBitOffset = 128;//With 8 bits, the mid value is 128 instead of 0. This requires an offset to be added
			
			double[][] channelSeparatedData = new double[nbChannels][samplesPerChannel];
			//Separates the data from the different channels
			for (int channel = 0; channel < nbChannels; channel++) {
				initialOffsetPerChannel = channel * storedBytesPerSample;
				for (int sample = 0; sample < samplesPerChannel; sample++) {
					//8 bits samples are unsigned
					if (validBitsPerSample > 8) channelSeparatedData[channel][sample] = ByteManipulationTools.getLittleEndianValueSigned(rawData, initialOffsetPerChannel + sampleByteOffset * sample, storedBytesPerSample);
					else channelSeparatedData[channel][sample] = ByteManipulationTools.getLittleEndianValueUnsigned(rawData, initialOffsetPerChannel + sampleByteOffset * sample, storedBytesPerSample) - eightBitOffset;
				}
			}
			return channelSeparatedData;
			/*Extra info about what was done above
			System.out.println(channelSeparatedData[0][1] + " :" + rawData[12] +" "+ rawData[13]);
			for (int i = 0; i < 200; i++) {
				System.out.println(channelSeparatedData[0][i*250] + " :" + rawData[sampleByteOffset * i*250] +" "+ rawData[sampleByteOffset * i*250 + 1]);
			}
			System.out.println();
			*/
		}
		return null;
	}
	/**
	 * Finds the time it takes to play the file
	 * @return The playtime
	 */
	public double findTime() {
		Chunk_data data;
		Chunk_fact fact;
		Chunk_fmt fmt;

		int nbBlocks;//Number of times a value is sampled into different channels
		int sampleRate;
		double time = 0;
		try {
			data = (Chunk_data) subChunks.get("subchunksAndInfo.Chunk_data");
			fact = (Chunk_fact) subChunks.get("subchunksAndInfo.Chunk_fact");;
			fmt = (Chunk_fmt) subChunks.get("subchunksAndInfo.Chunk_fmt");
			sampleRate = fmt.getSampleRate();
			if (fact != null) {
				nbBlocks = fact.getNbSampleFrames();
			}
			else {
				int totalBytes = data.getSubChunkSize();
				int bytesPerBlock = fmt.getBlockAlign();
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
	 * Gets information about the Wav file
	 * @return The information
	 */
	public String getFileInfo() {
		return fileInfo;
	}
	/**
	 * Gets data from each channels
	 * @return The data
	 */
	public double[][] getChannelSeparatedData() {
		return channelSeparatedData;
	}
//	/**
//	 * Gets the sampling rate of the file
//	 * @return The sampling rate
//	 */
//	public int getSampleRate() {
//		return sampleRate;
//	}
//	/**
//	 * Gets the name of the channels used
//	 * @return The channels used
//	 */
//	public String getChannelsUsed() {
//		return channelsUsed;
//	}
//	/**
//	 * Gets the name of the channels used with long names
//	 * @return The channels used with long names
//	 */
//	public String getChannelsUsedLongNames() {
//		return channelsUsedLongNames;
//	}
	/**
	 * Gets all the subchunks
	 * @return The subchunks
	 */
	public LinkedHashMap<String, SubChunks> getSubChunks() {
		return subChunks;
	}
	
	
	
	
}
