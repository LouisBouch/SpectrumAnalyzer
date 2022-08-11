package tools;

public class ByteManipulationTools {
	/**
	 *Takes a signed byte in a 2-'s complement disposition and converts it into the unsigned version of the same binary code
	 * @param signedByte The signer byte
	 * @return The unsigned equivalent
	 */
	public static int unsignedVersionOfByteTwosComplement(byte signedByte) {
		return (256 + signedByte);
	}
	
	/**
	 * Computes the value of consecutive bytes in the little endian format. Signed using two's complement
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getLittleEndianValueSigned(int[] data, int start, int nbBytes) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			value += temp * Math.pow(256, i);
		}
		//if the last bit is a 1, gets the negative of the two's complement
		if (value >= (int) Math.pow(2, nbBytes * 8 - 1)) value -= Math.pow(256, nbBytes);
		return value;
	}
	/**
	 * Computes the value of consecutive bytes in the little endian format. Signed using two's complement
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getLittleEndianValueSigned(byte[] data, int start, int nbBytes) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			value += temp * Math.pow(256, i);
		}
		//if the last bit is a 1, gets the negative of the two's complement
		if (value >= (int) Math.pow(2, nbBytes * 8 - 1)) value -= Math.pow(256, nbBytes);
		return value;
	}
	
	/**
	 * Computes the value of consecutive bytes in the little endian format
	 * @param start The starting point of the bytes to compute
	 * @param data The data to take the bytes from
	 * @param nbBytes The number of bytes to compute starting for the "start" byte
	 * @return The value of the bytes
	 */
	public static double getLittleEndianValueUnsigned(int[] data, int start, int nbBytes) {
		double value = 0;
		double temp;
		for (int i = 0; i < nbBytes; i++) {
			temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
			value += temp * Math.pow(256, i);
		}
		return value;
	}
		/**
		 * Computes the value of consecutive bytes in the little endian format
		 * @param start The starting point of the bytes to compute
		 * @param data The data to take the bytes from
		 * @param nbBytes The number of bytes to compute starting for the "start" byte
		 * @return The value of the bytes
		 */
		public static double getLittleEndianValueUnsigned(byte[] data, int start, int nbBytes) {
			double value = 0;
			double temp;
			for (int i = 0; i < nbBytes; i++) {
				temp = data[start + i] < 0 ? data[start + i] + 256 : data[start + i];
				value += temp * Math.pow(256, i);
			}
			return value;
	}
	/**
	 * Takes a decimal value and returns the array of bits corresponding to it
	 * @param value The value to convert
	 * @return The bit array (stored in bytes)
	 */
	public static byte[] decimalToBits(int value) {
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

}
