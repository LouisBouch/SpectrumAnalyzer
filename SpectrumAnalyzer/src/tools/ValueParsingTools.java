package tools;

public class ValueParsingTools {
	/**
	 * Takes in a time and transforms it into a string that gives days, hours, minutes, seconds and milliseconds.
	 * @param time Time in seconds
	 * @return The time
	 */
	public static String refinedTime(double time) {
		String timeString = "";
		
		double milliS = 1/1000.0;
		double minute = 60;
		double hour = minute * 60;
		double day = hour * 24;
		
		int nbMilliS;
		int nbMinutes;
		int nbSeconds;
		int nbHours;
		int nbDays;
		//Counts the days
		if (time > day) {
			nbDays = (int) (time / day);
			time = time % day;
			timeString += nbDays + " dy";
		}
		//Counts the hours
		if (time > hour) {
			nbHours = (int) (time / hour);
			time = time % hour;
			timeString += " " + nbHours + " hr";
		}
		//Counts the minutes
		if (time > minute) {
			nbMinutes = (int) (time / minute);
			time = time % minute;
			timeString += " " + nbMinutes + " min";
		}
		//Counts the seconds
		if (time > 1) {
			nbSeconds = (int) (time);
			time = time % 1;
			timeString += " " + nbSeconds + " sec";
		}
		//Counts the milliseconds
		if (time > milliS) {
			nbMilliS = (int) Math.round((time / milliS));
			time = time % milliS;
			timeString += " " + nbMilliS + " ms";
		}
		return timeString;
	}//End refinedTime
	/**
	 *  Takes a number, truncates it to a smaller number and adds the appropriate suffix
	 * @param number The number to put metrics to
	 * @return The refined way of printing it
	 */
	public static String refinedMetrics(double number) {
		String suffix = "";
		double divisor = 1;
		if (number > 1E6) {
			suffix = "M";
			divisor = 1E6;
		}
		else if (number > 1E3) {
			suffix = "K";
			divisor = 1E3;
		}
		return NumberManipulationTools.setDecimalPlaces(number/divisor, 3) + " " + suffix;
	}//End refinedByteSize
}
