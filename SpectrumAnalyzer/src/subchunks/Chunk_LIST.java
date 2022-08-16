package subchunks;

import java.util.HashMap;

import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_LIST extends SubChunks{
	private String info;
	
	private HashMap<String, String> headers = new HashMap<String, String>();
	
	private String typeID;
	public Chunk_LIST(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setInfo();
	}//End Chunk_LIST
	/**
	 * Handles the information
	 */
	public void setInfo() {
		info = "";
		typeID = ByteManipulationTools.getStringFromBytes(getData(), 0, 4);
		if (typeID.equals("INFO")) {
			setHeader();
			String header;
			String text;
			int size;
			int byteOffset = 4;
			
			while (byteOffset < getData().length) {
				header = ByteManipulationTools.getStringFromBytes(getData(), byteOffset, 4);
				byteOffset += 4;
				size = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
				byteOffset += 4;
				header = headers.get(header) != null ? headers.get(header) : header;
				text = ByteManipulationTools.getStringFromBytes(getData(), byteOffset, size);
				byteOffset += size;
				
				info += header + ":<br/>" + text + "<br/>";
			}//End while loop
			this.setInfo(info);
		}//End if
		else if (typeID.equals("adtl")) {
			String header;
			String text;
			int ID;
			int size;
			int byteOffset = 4;
			
			while (byteOffset < getData().length) {
				header = ByteManipulationTools.getStringFromBytes(getData(), byteOffset, 4); 
				byteOffset += 4;
				size = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
				byteOffset += 4;
				ID = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset, 4, ByteManipulationTools.LITTLEENDIAN);
				byteOffset += 4;
//				System.out.println(ByteManipulationTools.getStringFromBytes(getData(), byteOffset, size - 4));
				byteOffset += size - 4;
			}
		}
	}//End setInfo
	/**
	 * Initializes the hashMap
	 */
	public void setHeader() {
		headers.put("IARL", "Archive location");
		headers.put("IART", "Artist");
		headers.put("ICMS", "Commissioner");
		headers.put("ICMT", "Comments");
		headers.put("ICOP", "Copyright info");
		headers.put("ICRD", "Creation date");
		headers.put("ICRP", "Cropping information");
		headers.put("IDIM", "Original dimensions");
		headers.put("IDPI", "Dots per inch");
		headers.put("IENG", "Engineer");
		headers.put("IGNR", "Genre");
		headers.put("IKEY", "Keywords");
		headers.put("ILGT", "Lightness settings");
		headers.put("IMED", "Original medium");
		headers.put("INAM", "Title");
		headers.put("IPLT", "Number of colors");
		headers.put("IPRD", "Original name");
		headers.put("ISBJ", "Subject");
		headers.put("ISFT", "Software package");
		headers.put("ISRC", "Original subject author");
		headers.put("ISRF", "Source form");
		headers.put("ITCH", "Technician");
	}//End setHeader
}
//https://www.recordingblogs.com/wiki/list-chunk-of-a-wave-file
//https://www.recordingblogs.com/wiki/associated-data-list-chunk-of-a-wave-file