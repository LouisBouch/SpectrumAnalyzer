package subchunkObjects;

public class List_Cue {
	/**
	 * Stores the text linked to the cue
	 */
	private String text;
	/**
	 * The stored cue
	 */
	private Cue cue;
	/**
	 * Creates a List_cue
	 * @param cue The cue
	 * @param text The cue's text
	 */
	public List_Cue(Cue cue, String text) {
		this.cue = cue;
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Cue getCue() {
		return cue;
	}
	public void setCue(Cue cue) {
		this.cue = cue;
	}
	
	@Override
	public String toString() {
		if (cue != null) return cue.toString() + "; " + text;
		return "";
	}

}
