package subchunkObjects;


public class Id3 {
	/**
	 * The information header
	 */
	private String header;
	/**
	 * The information for the header
	 */
	private String text;
	/**
	 * Creates an id3
	 * @param header The information header
	 * @param text The information for the header
	 */
	public Id3(String header, String text) {
		this.header = header;
		this.text = text;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return header + ": " + text;
	}//End toString
}