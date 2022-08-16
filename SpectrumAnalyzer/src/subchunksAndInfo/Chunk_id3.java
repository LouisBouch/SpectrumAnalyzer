package subchunksAndInfo;

import java.util.HashMap;

import tools.ByteManipulationTools;
import wavParsingAndStoring.WavInfo;

public class Chunk_id3 extends SubChunks{
	private String info = "";
	
	private HashMap<String, String> headers = new HashMap<String, String>();
	
//	private String tag;
	private int majorVersion;
//	private int revisionNumber;
//	private int size;
	private boolean unsync = false;
	private boolean compression = false;
	
	
	public Chunk_id3(String subChunkName, int subChunkSize, byte[] data, WavInfo infoReservoir, boolean paddingByte) {
		super(subChunkName, subChunkSize, data, infoReservoir, paddingByte);
		setHeaders();
		setInfo();
	}//End Chunk_id3
	/**
	 * Obtains the headers and general information about the file
	 */
	public void setInfo() {
//		tag = ByteManipulationTools.getStringFromBytes(getData(), 0, 3);
		majorVersion = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), 3, 1, ByteManipulationTools.BIGENDIAN);
//		revisionNumber = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), 4, 1, ByteManipulationTools.BIGENDIAN);
		if (majorVersion == 2 || majorVersion == 3 || majorVersion == 4) {//Checks the version
			byte[]  bits = ByteManipulationTools.decimalToBits(ByteManipulationTools.getDecimalValueUnsigned(getData(), 5, 1, ByteManipulationTools.BIGENDIAN), 8) ;
			if (bits[0] == 1 ) unsync = true;
			if (bits[1] == 1 ) compression = true;
			if (unsync || compression) return;//I don't yet know what to do when those flags are set

			int byteOffset = 10;
			String header;
			String text;
			int headerSize;
			while (byteOffset < getData().length) {
				header = ByteManipulationTools.getStringFromBytes(getData(), byteOffset, 4);
				header = headers.get(header) != null ? headers.get(header) : header;
				headerSize = (int) ByteManipulationTools.getDecimalValueUnsigned(getData(), byteOffset + 4, 4, ByteManipulationTools.BIGENDIAN);
				byteOffset += 10;//4 header size, 4 size container, 2 flags = 10
				text = ByteManipulationTools.getStringFromBytes(getData(), byteOffset, headerSize);
				byteOffset += headerSize;
				
				info += header + ":<br/>" + text + "<br/><br/>";
			}//End while loop
		}
		this.setInfo(info);
	}//End getIDs
	/**
	 * Puts the headers inside the hashmap
	 */
	public void setHeaders() {
		headers.put("AENC", "Audio encryption");
		headers.put("COMM", "Comments");
		headers.put("APIC", "Attached picture");
		headers.put("COMR", "Commercial frame");
		headers.put("ENCR", "Encryption method registration");
		headers.put("EQUA", "Equalization");
		headers.put("ETCO", "Event timing codes");
		headers.put("GEOB", "General encapsulated object");
		headers.put("GRID", "Group identification registration");
		headers.put("IPLS", "Involved people list");
		headers.put("LINK", "Linked information");
		headers.put("MCDI", "Music CD identifier");
		headers.put("MLLT", "MPEG location lookup table");
		headers.put("OWNE", "Ownership frame");
		headers.put("PRIV", "Private frame");
		headers.put("PCNT", "Play counter");
		headers.put("POPM", "Popularimeter");
		headers.put("POSS", "Position synchronisation frame");
		headers.put("RBUF", "Recommended buffer size");
		headers.put("RVAD", "Relative volume adjustment");
		headers.put("RVRB", "Reverb");
		headers.put("SYLT", "Synchronized lyric/text");
		headers.put("SYTC", "Synchronized tempo codes");
		headers.put("TALB", "Album/Movie/Show title");
		headers.put("TBPM", "BPM (beats per minute)");
		headers.put("TCOM", "Composer");
		headers.put("TCON", "Content type");
		headers.put("TCOP", "Copyright message");
		headers.put("TDAT", "Date");
		headers.put("TDLY", "Playlist delay");
		headers.put("TENC", "Encoded by");
		headers.put("TEXT", "Lyricist/Text writer");
		headers.put("TFLT", "File type");
		headers.put("TIME", "Time");
		headers.put("TIT1", "Content group description");
		headers.put("TIT2", "Title/songname/content description");
		headers.put("TIT3", "Subtitle/Description refinement");
		headers.put("TKEY", "Initial key");
		headers.put("TLAN", "Language(s)");
		headers.put("TLEN", "Length");
		headers.put("TMED", "Media type");
		headers.put("TOAL", "Original album/movie/show title");
		headers.put("TOFN", "Original filename");
		headers.put("TOLY", "Original lyricist(s)/text writer(s)");
		headers.put("TOPE", "Original artist(s)/performer(s)");
		headers.put("TORY", "Original release year");
		headers.put("TOWN", "File owner/licensee");
		headers.put("TPE1", "Lead performer(s)/Soloist(s)");
		headers.put("TPE2", "Band/orchestra/accompaniment");
		headers.put("TPE3", "Conductor/performer refinement");
		headers.put("TPE4", "Interpreted, remixed, or otherwise modified by");
		headers.put("TPOS", "Part of a set");
		headers.put("TPUB", "Publisher");
		headers.put("TRCK", "Track number/Position in set");
		headers.put("TRDA", "Recording dates");
		headers.put("TRSN", "Internet radio station name");
		headers.put("TRSO", "Internet radio station owner");
		headers.put("TSIZ", "Size");
		headers.put("TSRC", "ISRC (international standard recording code)");
		headers.put("TSSE", "Software/Hardware and settings used for encoding");
		headers.put("TYER", "Year");
		headers.put("TXXX", "User defined text information frame");
		headers.put("UFID", "Unique file identifier");
		headers.put("USER", "Terms of use");
		headers.put("USLT", "Unsychronized lyric/text transcription");
		headers.put("WCOM", "Commercial information");
		headers.put("WCOP", "Copyright/Legal information");
		headers.put("WOAF", "Official audio file webpage");
		headers.put("WOAR", "Official artist/performer webpage");
		headers.put("WOAS", "Official audio source webpage");
		headers.put("WORS", "Official internet radio station homepage");
		headers.put("WPAY", "Payment");
		headers.put("WPUB", "Publishers official webpage");
		headers.put("WXXX", "User defined URL link frame");
		
	}
}
//https://id3.org/id3v2.3.0