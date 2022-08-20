package subchunkObjects;


public class List_Header {
	/**
	 * The header name
	 */
	private String header;
	/**
	 * The header's text
	 */
	private String text;
	
	/**
	 * Creates a new header
	 * @param header The header name
	 * @param text The header's text
	 */
	public List_Header (String header, String text) {
		this.header = header;
		this.text = text;
	}
	@Override
	public String toString() {
		return header + ": " + text;
	}//End toString
}
