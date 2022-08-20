package subchunkObjects;

import java.util.ArrayList;

import interfaces.Info;
import tools.StringTools;

public class Id3Info implements Info {
	/**
	 * Each id contains information about the wav file
	 */
	private ArrayList<Id3> ids = new ArrayList<Id3>();
	
	
	public ArrayList<Id3> getIds() {
		return ids;
	}


	public void setIds(ArrayList<Id3> ids) {
		this.ids = ids;
	}
	
	/**
	 * Adds a new id using a header and the header information
	 * @param header Information header
	 * @param text Information
	 * @return Returns the added id3
	 */
	public Id3 addId3(String header, String text) {
		Id3 id3 = new Id3(header, text);
		ids.add(id3);
		return id3;
	}
	/**
	 * Adds a new id
	 * @param id The id to add
	 * @return Returns the added id3
	 */
	public Id3 addId3(Id3 id) {
		ids.add(id);
		return id;
	}

	@Override
	public String toString() {
		String idsString = "<B>Additional information about the audio file:</B><br/>";
		idsString += StringTools.arrayListToString(ids);
		return idsString;
	}//End toString
}
