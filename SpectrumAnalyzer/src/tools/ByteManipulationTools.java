package tools;

public class ByteManipulationTools {
	public static final int LITTLEENDIAN = 0;
	public static final int BIGENDIAN = 1;
	
	public static final int TWOSCOMPLEMENT = 2;
	public static final int SIGNEDMAGNITUDE = 3;
	public static final int UNSIGNED = 4;
	/**
	 *Takes a signed byte in a 2-'s complement disposition and converts it into the unsigned version of the same binary code
	 * @param signedByte The signer byte
	 * @return The unsigned equivalent
	 */
	public static int unsignedVersionOfByteTwosComplement(byte signedByte) {
		return (256 + signedByte);
	}
	/**
	 *Takes a signed byte in a 2-'s complement disposition and converts it into the unsigned version of the same binary code
	 * @param signedByte The signer byte
	 * @return The unsigned equivalent
	 */
	public static int unsignedVersionOfByteTwosComplement(int signedByte) {
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
			if (endianness == BIGENDIAN ) value += temp * Math.pow(256, nbBytes - 1 - i);
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
			if (endianness == BIGENDIAN ) value += temp * Math.pow(256, nbBytes - 1 - i);
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
			if (endianness == BIGENDIAN ) value += temp * Math.pow(256, nbBytes - 1 - i);
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
			if (endianness == BIGENDIAN ) value += temp * Math.pow(256, nbBytes - 1 - i);
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
	public static double getFloatingP32(int bytes) {
		//Get sign
		int sign = bytes & 0x80000000;
		//Get exponent
		int exp = ((bytes & 0x7F800000) >> 23) - 127;
		//Get denormalization
		boolean denormalized = exp == -127 ? true : false;
		exp = denormalized ? -126 : exp;
		//Mantissa
		int mantissa = bytes & 0x7FFFFF;
		mantissa += denormalized ? 0 : 0x800000;//second equation equivalent to 1 << 23
		//Final answer
		double answer = mantissa * Math.pow(2, -23 + exp);
		return sign == 0 ? answer : -answer;
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
	public static byte[] decimalToBits(double value, int algorithm) {
		boolean negative = value  < 0 ? true : false;
		value = Math.abs(value);
		if (value == 0) return new byte[1];
		byte leftOver;
		double newValue = value;
		int size = (int) Math.floor(Math.log(value)/Math.log(2) + 1);
		if (algorithm != UNSIGNED) size++;
		int index = 1;
		byte[] bits = new byte[size];
		while (newValue != 0) {
			leftOver = (byte) (newValue%2);
			newValue = (int) (newValue/2);
			bits[size - index] = leftOver;
			index++;
		}
		//handles negatives
		if (negative && algorithm == SIGNEDMAGNITUDE) {
			bits[0] = 1;
		}
		if (negative && algorithm == TWOSCOMPLEMENT) {
			boolean oneFound = false;
			int bit = bits.length - 1;
			while (bit >= 0) {
				if (oneFound) bits[bit] = bits[bit] == 1 ? 0 : (byte) 1;
				else if (bits[bit] == 1) oneFound = true;
				bit--;
			}
		}
		return bits;
	}
	
	/**
	 * Takes a decimal value and returns the array of bits corresponding to it
	 * @param value The value to convert
	 * @param size The size of the bit array
	 * @return The bit array (stored in bytes)
	 */
	public static byte[] decimalToBits(double value, int size, int algorithm) {
		boolean negative = value  < 0 ? true : false;
		value = Math.abs(value);
		if (value == 0) return new byte[size];
		byte leftOver;
		double newValue = value;
		int minSize = (int) Math.floor(Math.log(value)/Math.log(2) + 1);
		if (algorithm != UNSIGNED) minSize++;
		if (size < minSize) return null; 
		
		int index = 1;
		byte[] bits = new byte[size];
		while (newValue != 0 && size - index >= 0) {
			leftOver = (byte) (newValue%2);
			newValue = (int) (newValue/2);
			bits[size - index] = leftOver;
			index++;
		}
		//handles negatives
		if (negative && algorithm == SIGNEDMAGNITUDE) {
			bits[0] = 1;
		}
		if (negative && algorithm == TWOSCOMPLEMENT) {
			boolean oneFound = false;
			int bit = bits.length - 1;
			while (bit >= 0) {
				if (oneFound) bits[bit] = bits[bit] == 1 ? 0 : (byte) 1;
				else if (bits[bit] == 1) oneFound = true;
				bit--;
			}
		}
		return bits;
	}
	/**
	 * Decodes A-Law
	 * @param byteInfo The byte to decode
	 * @return The decoded byte
	 */
	public static int decodeALaw(byte byteInfo) {
		//Invert every other bit and the sign. (1101 0101)
		byteInfo ^= 0xD5;
		//Pulls out the sign
		int sign = byteInfo & 0x80;
		//Pull out the exponent
		int exp = (byteInfo & 0x70) >> 4;
		//Pull out the mantissa
		int data = byteInfo & 0x0F;
		
		//Shift data to the left
		data <<= 4;
		//Add 8 to put the result in the middle of the range
		data += 8;
		//If normalized (exp != 0) add one before the data
		if (exp != 0) data += 0x100;
		//Shift the final data by the exponent - 1
		if (exp > 1) data <<= (exp - 1);
		//Add the sign
		return sign == 0 ? data : -data;
	}
	/**
	 * Decodes A-Law
	 * @param byteInfo The byte to decode
	 * @return The decoded byte
	 */
	public static int decodeULaw(byte byteInfo) {
		//Flip all the bits
		byteInfo = (byte) ~byteInfo;
		//Pull the sign
		int sign = byteInfo & 0x80;
		//Pull out and shift exponent
		int exp = (byteInfo & 0x70) >> 4;		
		//Pull out data
		int data = byteInfo & 0x0F;
		//Add the fifth bit
		data |= 0x10;
		//Shift and add 1
		data <<= 1;
		data += 1;
		//Shift by the necessary amount
		data <<= exp + 2;
		//Remove original bias of 132
		data -= 132;
		//Add the sign if needed
		return sign == 0 ? data : -data;
		
	}
}
