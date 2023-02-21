package com.nic.transport.util;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {

	@Value("IMAGE_COMPRESSION_RESIZE")
	private static String imageCompressionResize;
	
	static ArrayList<ImageResizeSettings> imageResizeSettings = new ArrayList();
	
	public static ArrayList<ImageResizeSettings> getImageResizeSettings(String data) {

		if(imageResizeSettings.isEmpty()) {
			
			String [] arr1 = data.split("\\|");
			for(int i =0 ; i< arr1.length ; i++) {
				
				String [] arr2 = arr1[i].split(",");
				ImageResizeSettings obj = new ImageResizeSettings();
				obj.setSizeInMB(Integer.parseInt(arr2[0]));
				obj.setResizeHeight(Integer.parseInt(arr2[1]));
				obj.setResizeWidth(Integer.parseInt(arr2[2]));
				imageResizeSettings.add(obj);
				
			}
		}
		return imageResizeSettings;
		
	}
	public static ImageResizeSettings getImageResizeSettings(int actualSize) {
		if (!imageResizeSettings.isEmpty()) {
		return imageResizeSettings.
				stream().
				filter(p-> p.getSizeInMB() == actualSize).
				findFirst().
				get();
		}
		return null;
		
		
	}
	static {
		getImageResizeSettings(imageCompressionResize);
	}
	
}
