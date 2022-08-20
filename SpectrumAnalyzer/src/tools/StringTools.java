package tools;

import java.util.ArrayList;

public class StringTools {
	/**
	 * Prints an array list with each element on a different line using html's new lines
	 * @param list The list to parse
	 * @return The string formed by the method
	 */
	public static String arrayListToString(ArrayList<?> list) {
		String string = "";
		for (int index = 0; index < list.size(); index++) {
			string += "<br/>";
			string += list.get(index).toString();
		}
		return string;
	}
}
