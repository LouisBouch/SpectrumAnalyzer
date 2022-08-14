package tools;

public class NumberManipulationTools {
	
	/**
	 * Truncates a double to keep only a certain amount of decimal places
	 * @param number Number to truncate
	 * @param decimalPlaces Decimal places to keep
	 * @return The truncated double
	 */
	public static double setDecimalPlaces(double number, int decimalPlaces) {
		return ((int) (number * Math.pow(10, decimalPlaces))) / Math.pow(10, decimalPlaces);
	}

}
