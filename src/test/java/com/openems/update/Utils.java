package com.openems.update;

import java.util.Arrays;

public class Utils {

	/**
	 * The integer value "sizeOfTheUpdateFile" is converted in to byte array.
	 * 
	 * @param sizeOfTheUpdateFile example size = 576 (kb)
	 * @return Byte array of the size
	 */
	public static byte[] hexStringToByteArray(int sizeOfTheUpdateFile, int byteLength, int numberofBytes) {
		String s = Integer.toHexString(sizeOfTheUpdateFile);
		byte[] data = new byte[numberofBytes];

		StringBuilder sb = new StringBuilder();
		while (sb.length() < byteLength - s.length()) {
			sb.append('0');
		}
		sb.append(s);

		for (int i = 0; i < sb.length() / 2; i++) {

//			System.out.println("Substring is : " + sb.substring(2 * i, 2 * i + 2) + //
//					" parsed int is " + Integer.parseInt(sb.substring(2 * i, 2 * i + 2), 16) + //
//					" Shifting : " + (0xFF & (Integer.parseInt(sb.substring(2 * i, 2 * i + 2), 16))) +//
//					" into bytes : " + (byte) (0xFF & (Integer.parseInt(sb.substring(2 * i, 2 * i + 2), 16))));
			
			
			data[i] = (byte) (0xFF & (Integer.parseInt(sb.substring(2 * i, 2 * i + 2), 16)));
		}

		return data;
	}

	/**
	 * Convert the Whole update file byte array in to the payload
	 * 
	 * @param actualArray
	 * @param dataSize
	 * @return payload to for making fc42 multiple times
	 */
	public static byte[][] getData(byte[] actualArray, int dataSize) {

		int len = actualArray.length;

		int m = len / dataSize;

		int counter = 0;

		byte[][] newArray = new byte[m + 1][dataSize];

		for (int i = 0; i < len - dataSize + 1; i += dataSize)
			newArray[counter++] = Arrays.copyOfRange(actualArray, i, i + dataSize);

		if (len % dataSize != 0)
			newArray[counter] = Arrays.copyOfRange(actualArray, len - len % dataSize, len);

		return newArray;

	}

}
