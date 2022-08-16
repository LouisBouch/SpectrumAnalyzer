package subchunks;

import wavParsingAndStoring.WavInfo;

public class SubChunks {
	private String subChunkName;
	private int subChunkSize;
	private String info = "NO AVAILABLE INFO";
	private byte[] data;
	private boolean paddingByte;
	private WavInfo infoReservoir;
	
	/**
	 * Creates a chunk
	 * @param subChunkName The name of the subchunk
	 * @param subChunkSize The size of the data in the subchunk (Does not count the 4 bits for the name and 4 bits for the size)
	 * @param data The binary data of the subchunk
	 * @param paddingByte True if a padding byte is added, false otherwise
	 */
	public SubChunks(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		this.subChunkName = subChunkName;
		this.subChunkSize = subChunkSize;
		this.data = data;
		this.paddingByte = paddingByte;
		this.infoReservoir = infoReservoir;
	}
	/**
	 * Gets the name of the subchunk
	 * @return subchunk name
	 */
	public String getSubChunkName() {
		return subChunkName;
	}
	/**
	 * Gets the size of the subchunk
	 * @return subchunk size
	 */
	public int getSubChunkSize() {
		return subChunkSize;
	}
	/**
	 * Gets the data of the subchunk
	 * @return subchunk data
	 */
	public byte[] getData() {
		return data;
	}
	/**
	 * Gets the info of the subchunk
	 * @return data info
	 */
	public String getInfo() {
		return info;
	}
	/**
	 * Allows for the info field to be set
	 * @param info String of information about the subchunk
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	/**
	 * Checks if a padding byte was added
	 * @return Whether the padding bit was added or not 
	 */
	public boolean isPaddingByte() {
		return paddingByte;
	}
	/**
	 * Gets all the information about the wav file
	 * @return The info reservoir
	 */
	public WavInfo getInfoReservoir() {
		return infoReservoir;
	}
	@Override
	public String toString() {
		return "<B>subchunk:</B> " + subChunkName + "<br/>" + info;
	}
}
