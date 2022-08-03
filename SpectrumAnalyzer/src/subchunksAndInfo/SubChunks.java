package subchunksAndInfo;

public class SubChunks {
	private String subChunkName;
	private int subChunkSize;
	private String info = "NO AVAILABLE INFO";
	private byte[] data;
	private WavInfo infoReservoir;
	private boolean paddingBit;
	
	public SubChunks(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		this.subChunkName = subChunkName;
		this.subChunkSize = subChunkSize;
		this.data = data;
		this.infoReservoir = infoReservoir;
		this.paddingBit = paddingByte;
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
	 * Gets the infoReservoir
	 * @return The infoReservoir
	 */
	public WavInfo getInfoReservoir() {
		return infoReservoir;
	}
	/**
	 * Allows for the info field to be set
	 * @param info String of information about the subchunk
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	/**
	 * Checks if a padding bit was added
	 * @return Whether the padding bit was added or not 
	 */
	public boolean isPaddingBit() {
		return paddingBit;
	}
	@Override
	public String toString() {
		return "<B>subchunk:</B> " + subChunkName + "<br/>" + info;
	}
}
