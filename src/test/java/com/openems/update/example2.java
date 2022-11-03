package openems;

public class example2 {

	public static void main(String args[]) {

		byte[] frameNumber = new byte[2];
//		setFrameNumber(frameNumber, 1);
//		setFrameNumber(frameNumber, 10);
//		setFrameNumber(frameNumber, 100);
//		setFrameNumber(frameNumber, 256);
//		setFrameNumber(frameNumber, 3000);
//		setFrameNumber(frameNumber, 257);
//
		//hexStringToByteArray(256, 4, 2()
		
		
		byte[] x = hexStringToByteArray(256, 4, 2);
		
		System.out.print(256 + " = ");
		for (byte b : x) {
			System.out.print(b + " | ");
		}
		System.out.println();
		
		x = hexStringToByteArray(257, 4, 2);
		
		System.out.print(257 + " = ");
		for (byte b : x) {
			System.out.print(b + " | ");
		}
		
		System.out.println();
		x = hexStringToByteArray(511, 4, 2);
		
		System.out.print(511 + " = ");
		for (byte b : x) {
			System.out.print(b + " | ");
		}
		
		
		System.out.println();
		x = hexStringToByteArray(512, 4, 2);
		
		System.out.print(512 + " = ");
		for (byte b : x) {
			System.out.print(b + " | ");
		}
		
	}

	public static void setFrameNumber(byte[] frameNumber, int frameNum) {
		
		byte[] frameByte = new byte[2];
		frameByte[0] = (byte) (frameNum & 0xFF);
		frameByte[1] = (byte) ((frameNum << 8) & 0xFF);

		for (int i = 0; i < frameByte.length; i++) {
			frameNumber[i] = frameByte[i];
		}
		System.out.print(frameNum + " = ");
		for (byte b : frameByte) {
			System.out.print(b + " | ");
		}
		System.out.println();
	}
	
	
	public static byte[] hexStringToByteArray(int sizeOfTheUpdateFile, int byteLength, int numberofBytes) {
		String s = Integer.toHexString(sizeOfTheUpdateFile);
		byte[] data = new byte[numberofBytes];

		StringBuilder sb = new StringBuilder();
		while (sb.length() < byteLength - s.length()) {
			sb.append('0');
		}
		sb.append(s);

		for (int i = 0; i < sb.length() / 2; i++) {
			data[i] = (byte) (0xFF & (Integer.parseInt(sb.substring(2 * i, 2 * i + 2), 16)));
		}

		return data;
	}

}

//String s =
// "014282000141544C5244355050455353313930345A002E002020200008ACC390D3A07A1471015A000001001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009FB6";

// System.out.println(s.length() / 2);