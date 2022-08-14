package tools;

public class ByteManipulationTools {
	public static final int BIGENDIAN = 1;
	public static final int LITTLEENDIAN = 0;
	/**
	 *Takes a signed byte in a 2-'s complement disposition and converts it into the unsigned version of the same binary code
	 * @param signedByte The signer byte
	 * @return The unsigned equivalent
	 */
	public static int unsignedVersionOfByteTwosComplement(byte signedByte) {
		return (256 + signedByte);
	}
	
	/**
	 * Computes the value of consecutive bytes. Signed using two's complement
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getDecimalValueSigned(int[] data, int start, int nbBytes, int endianness) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			if (endianness == 1 ) value += temp * Math.pow(256, nbBytes - 1 - i);
			else value += temp * Math.pow(256, i);
		}
		//if the last bit is a 1, gets the negative of the two's complement
		if (value >= (int) Math.pow(2, nbBytes * 8 - 1)) value -= Math.pow(256, nbBytes);
		return value;
	}
	/**
	 * Computes the value of consecutive bytes. Signed using two's complement
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getDecimalValueSigned(byte[] data, int start, int nbBytes, int endianness) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			if (endianness == 1 ) value += temp * Math.pow(256, nbBytes - 1 - i);
			else value += temp * Math.pow(256, i);
		}
		//if the last bit is a 1, gets the negative of the two's complement
		if (value >= (int) Math.pow(2, nbBytes * 8 - 1)) value -= Math.pow(256, nbBytes);
		return value;
	}
	
	/**
	 * Computes the value of consecutive bytes
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getDecimalValueUnsigned(int[] data, int start, int nbBytes, int endianness) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			if (endianness == 1 ) value += temp * Math.pow(256, nbBytes - 1 - i);
			else value += temp * Math.pow(256, i);
		}
		return value;
	}
	/**
	 * Computes the value of consecutive bytes
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getDecimalValueUnsigned(byte[] data, int start, int nbBytes, int endianness) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			if (endianness == 1 ) value += temp * Math.pow(256, nbBytes - 1 - i);
			else value += temp * Math.pow(256, i);
		}
		return value;
		
			
	}

	/**
	 * Computes the value of consecutive bits
	 * @param data The data array
	 * @param start The starting point
	 * @param nbBits Amount of bits to compute
	 * @return The final value
	 */
	public static double getDecimalFromBits(byte[] data, int start, int nbBits) {
		double value = 0;
		for (int bit = 0; bit < nbBits; bit++) {
			value += data[bit + start] * Math.pow(2, nbBits - 1 - bit);
		}
		return value;
	}
	/**
	 * Computes the value of consecutive bits
	 * @param data The data array
	 * @param start The starting point
	 * @param nbBits Amount of bits to compute
	 * @return The final value
	 */
	public static double getDecimalFromBits(int[] data, int start, int nbBits) {
		double value = 0;
		for (int bit = 0; bit < nbBits; bit++) {
			value += data[bit + start] * Math.pow(2, nbBits - 1 - bit);
		}
		return value;
	}
	
	/**
	 * Takes 4 bytes of data and computes the float representation using IEEE-754
	 * @param data The bytes
	 * @param start The starting point
	 * @return The float
	 */
	public static double getFloatingP32(byte[] data, int start, int endianness) {
		byte[] bits = decimalToBits(getDecimalValueUnsigned(data, start, 4, endianness), 32);
		
		int sign = bits[0] == 1 ? -1 : 1;
		int exp = (int) getDecimalFromBits(bits, 1, 8) - 127;
		boolean denormalized = exp == -127 ? true : false;
		double mantissa = denormalized ? 0 : 1;
		if (denormalized) exp = -126;
		int bitOffset = 9;
		
		for (int bit = bitOffset; bit < 32; bit++) {
			mantissa += bits[bit] * Math.pow(2, -(bit - bitOffset + 1));
		}
		
		return sign * Math.pow(2, exp) * mantissa;
	}
	/**
	 * Takes 4 bytes of data and computes the float representation using IEEE-754
	 * @param data The bytes
	 * @param start The starting point
	 * @return The float
	 */
	public static double getFloatingP32(int[] data, int start, int endianness) {
		byte[] bits = decimalToBits(getDecimalValueUnsigned(data, start, 4, endianness), 32);
		
		int sign = bits[0] == 1 ? -1 : 1;
		int exp = (int) getDecimalFromBits(bits, 1, 8) - 127;
		boolean denormalized = exp == -127 ? true : false;
		double mantissa = denormalized ? 0 : 1;
		if (denormalized) exp = -126;
		int bitOffset = 9;
		
		for (int bit = bitOffset; bit < 32; bit++) {
			mantissa += bits[bit] * Math.pow(2, -(bit - bitOffset + 1));
		}
		
		return sign * Math.pow(2, exp) * mantissa;
	}
	/**
	 * Gets the string from consecutive bytes
	 * @param data The data to take the information from
	 * @param start The starting point
	 * @param nbBytes The amount of characters to concatenate
	 * @return The string formed by the characters
	 */
	public static String getStringFromBytes(int[] data, int start, int nbBytes) {
		String string = "";
		for (int byt = 0; byt < nbBytes; byt++) {
			string += (char) data[byt + start];
		}
		return string;
	}
	/**
	 * Gets the string from consecutive bytes
	 * @param data The data to take the information from
	 * @param start The starting point
	 * @param nbBytes The amount of characters to concatenate
	 * @return The string formed by the characters
	 */
	public static String getStringFromBytes(byte[] data, int start, int nbBytes) {
		String string = "";
		for (int byt = 0; byt < nbBytes; byt++) {
			string += (char) data[byt + start];
		}
		return string;
	}
	/**
	 * Takes a decimal value and returns the array of bits corresponding to it
	 * @param value The value to convert
	 * @return The bit array (stored in bytes)
	 */
	public static byte[] decimalToBits(double value) {
		if (value == 0) return new byte[1];
		byte leftOver;
		double newValue = value;
		int size = (int) Math.floor(Math.log(value)/Math.log(2) + 1);
		int index = 1;
		byte[] bits = new byte[size];
		while (newValue != 0) {
			leftOver = (byte) (newValue%2);
			newValue = (int) (newValue/2);
			bits[size - index] = leftOver;
			index++;
		}
		return bits;
	}
	
	/**
	 * Takes a decimal value and returns the array of bits corresponding to it
	 * @param value The value to convert
	 * @param size The size of the bit array
	 * @return The bit array (stored in bytes)
	 */
	public static byte[] decimalToBits(double value, int size) {
		if (value == 0) return new byte[size];
		byte leftOver;
		double newValue = value;
		int index = 1;
		byte[] bits = new byte[size];
		while (newValue != 0 && size - index >= 0) {
			leftOver = (byte) (newValue%2);
			newValue = (int) (newValue/2);
			bits[size - index] = leftOver;
			index++;
		}
		return bits;
	}

}
