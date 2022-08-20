package wavParsingAndStoring;


import java.util.ArrayList;

import interfaces.Info;
import subchunkObjects.CueInfo;
import subchunkObjects.DataInfo;
import subchunkObjects.FactInfo;
import subchunkObjects.FormatInfo;
import subchunkObjects.Id3Info;
import subchunkObjects.ListInfo;
import subchunkObjects.PeakInfo;
import tools.ValueParsingTools;

public class WavInfo {
	
	//General information--------------------------------------------
	/**
	 * Name of the file
	 */
	private String fileName;
	/**
	 * Weight in bytes of the file
	 */
	private double weight;
	/**
	 * Weight of the file as a string (Contains a suffix to make it easier to read)
	 */
	private String weightString;
	/**
	 * Duration of the file in seconds
	 */
	private double time;
	/**
	 * Duration of the file as a string (String containing minutes, seconds....)
	 */
	private String timeString;
	
	
	//Subchunk information-------------------------------------------
	private ArrayList<Info> allInfo = new ArrayList<Info>();
	/**
	 * Contains the format information
	 */
	private FormatInfo formatInfo;
	/**
	 * Contains peak information
	 */
	private PeakInfo peakInfo;
	/**
	 * Contains the raw audio data and parsed versions
	 */
	private DataInfo dataInfo;
	/**
	 * Contains the amount of sample frames in the data file
	 */
	private FactInfo factInfo;
	/**
	 * Additional information about the audio file
	 */
	private Id3Info id3Info;
	/**
	 * Information about the cues
	 */
	private CueInfo cueInfo;
	/**
	 * Additional information about the audio file
	 */
	private ListInfo listInfo;
	/**
	 * Creates an object that contains all the information about the wav file
	 * @param data The data in bytes
	 * @param fileName The name of the file
	 */
	public WavInfo(byte[] data, String fileName) {
		this.fileName = fileName;
		weight = data.length;
	}//End WavInfo
	
	public String getFileName() {
		return fileName;
	}

	public FormatInfo getFormatInfo() {
		return formatInfo;
	}

	public void setFormatInfo(FormatInfo formatInfo) {
		this.formatInfo = formatInfo;
		allInfo.add(formatInfo);
	}

	public PeakInfo getPeakInfo() {
		return peakInfo;
	}

	public void setPeakInfo(PeakInfo peaks) {
		this.peakInfo = peaks;
		allInfo.add(peaks);
	}
	
	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo data) {
		this.dataInfo = data;
	}

	public FactInfo getFactInfo() {
		return factInfo;
	}

	public void setFactInfo(FactInfo factInfo) {
		this.factInfo = factInfo;
		allInfo.add(factInfo);
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
		weightString = ValueParsingTools.refinedMetrics(weight) + "b";
	}
	
	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
		timeString = ValueParsingTools.refinedTime(time);
	}
	
	public Id3Info getId3Info() {
		return id3Info;
	}

	public void setId3Info(Id3Info id3Info) {
		this.id3Info = id3Info;
		allInfo.add(id3Info);
	}
	
	public CueInfo getCueInfo() {
		return cueInfo;
	}

	public void setCueInfo(CueInfo cueInfo) {
		this.cueInfo = cueInfo;
//		allInfo.add(cueInfo);
	}

	public ListInfo getListInfo() {
		return listInfo;
	}

	public void setListInfo(ListInfo listInfo) {
		this.listInfo = listInfo;
		allInfo.add(listInfo);
	}

	@Override
	public String toString() {
		String string = "<html>" + "<B>Name:</B> " + fileName + "<br/><B>Weight:</B> " + weightString + "<br/><B>Audio length:</B> " + timeString;
		for (int index = 0; index < allInfo.size(); index++) {
			string += "<br/><br/>";
			string += allInfo.get(index).toString();
		}
		string += "<html>";
		return string;
	}//End toString
}
//String string = "";
//Field[] fields = getClass().getDeclaredFields();
//for (Field field : fields) {
//	if (Info.class.isAssignableFrom(field.getType())) {
//		System.out.println("is assignable");
//	}
//	if (field.getType().isArray()) {
//		System.out.println("array");
//	}
//}
