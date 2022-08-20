package subchunkObjects;

import java.util.ArrayList;

import interfaces.Info;
import tools.StringTools;

public class ListInfo implements Info {
	private ArrayList<List_Header> listHeaders = new ArrayList<>();
	private ArrayList<List_Cue> listCues= new ArrayList<>();
	
	/**
	 * Adds a header
	 * @param header The header name
	 * @param text The text that comes with the header
	 * @return Returns the header added
	 */
	public List_Header addList_Header(String headerName, String text) {
		List_Header header = new List_Header(headerName, text);
		listHeaders.add(header);
		return header;
	}
	/**
	 * Adds a header
	 * @param header The header to add
	 * @return Returns the header added
	 */
	public List_Header addList_Header(List_Header header) {
		listHeaders.add(header);
		return header;
	}
	/**
	 * Adds a cue
	 * @param cue The cue
	 * @param text The text that comes with the cue
	 * @return Returns the header added
	 */
	public List_Cue addList_Cue(Cue cue, String text) {
		List_Cue cueInfo = new List_Cue(cue, text);
		listCues.add(cueInfo);
		return cueInfo;
	}
	/**
	 * Adds a cue
	 * @param cue The cue to add
	 * @return Returns the cue added
	 */
	public List_Cue addList_Cue(List_Cue cueInfo) {
		listCues.add(cueInfo);
		return cueInfo;
	}
	
	public ArrayList<List_Header> getListHeaders() {
		return listHeaders;
	}



	public void setListHeaders(ArrayList<List_Header> listHeaders) {
		this.listHeaders = listHeaders;
	}



	public ArrayList<List_Cue> getListCues() {
		return listCues;
	}



	public void setListCues(ArrayList<List_Cue> listCues) {
		this.listCues = listCues;
	}



	@Override
	public String toString() {
		String listInfo = "";
		if (listHeaders.size() != 0) {
			listInfo += "<B>Additional information about the audio file:</B>";
			listInfo += StringTools.arrayListToString(listHeaders);
		}
		if (listCues.size() != 0 ) {
			if (listHeaders.size() != 0) listInfo +=  "<br/><br/>";
			listInfo += "<B>Information about cues:</B>";
			listInfo += StringTools.arrayListToString(listCues);
		}
		return listInfo;
	}//End toString
}
