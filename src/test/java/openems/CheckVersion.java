package openems;

import java.io.File;
import java.util.regex.Pattern;

public class CheckVersion {

	public static void main(String[] args) {

		String version = "0.1";
		String PATH = "C:\\ATLUPDATE\\feneconR0.0.bin";
		
		
		long size = new File (PATH).length();
		
		getFileSize (PATH);
		
		System.out.println(size);
		
		
		
		
//		String calculateVersion = GetVersion(PATH);
//		
//		System.out.println(calculateVersion);
//		
//		
//		float v1 = Float.parseFloat(version);
//		float v2 = Float.parseFloat(calculateVersion);
//		
//		if (v1 < v2) {
//			System.out.println("V1 is lesser then V2");
//		}
//		if (v1 > v2 ) {
//			System.out.println("V1 is greater then V2");
//		}
		
		

	}

	private static void getFileSize(String pATH) {
		// TODO Auto-generated method stub
		
	}

	private static String GetVersion(String PATH) {

		String[] parts = PATH.substring(PATH.lastIndexOf("\\") + 1, PATH.length())//
				.split(Pattern.quote("."));

		String version = parts[0].substring(parts[0].length() - 1);
		String subVersion = parts[1];

		return version + "." + subVersion;

	}

}
