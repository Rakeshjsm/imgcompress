package com.nic.transport.Service;

import java.io.File;
import java.text.DecimalFormat;

public class FileSize {
	
	/** FileSize
	* @param file
	*/
	public static String getSizeFile(File file){
		String size = "";
		if(file.length() >= 1048576){
			//get size MB
			size = new DecimalFormat("##.##").format((double)file.length() / (1024*1024))+"MB";
		}
		else {
			//get size KB
			size = new DecimalFormat("##.##").format((double)file.length() / 1024)+"KB";
		}
		return size;
	}

}
