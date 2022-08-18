package subchunkObjects;

import java.util.ArrayList;

import interfaces.Info;

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
	 */
	public void addId3(String header, String text) {
		ids.add(new Id3(header, text));
	}
	/**
	 * Adds a new id
	 * @param id The id to add
	 */
	public void addId3(Id3 id) {
		ids.add(id);
	}

	@Override
	public String toString() {
		String idsString = "<B>Additional information about the audio file:</B><br/>";
		for (int id = 0; id < ids.size(); id++) {
			idsString += ids.get(id).getHeader() + ": " + ids.get(id).getText() + "<br/>";
		}
		return idsString;
	}//End toString
}
