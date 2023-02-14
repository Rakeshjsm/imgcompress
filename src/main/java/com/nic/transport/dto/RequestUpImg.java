package com.nic.transport.dto;

public class RequestUpImg {
	

	byte [] imageContent;
	String fileName;
	int resizeWidth;
	int resizeHeight;
	


	public byte[] getImageContent() {
		return imageContent;
	}
	public void setImageContent(byte[] imageContent) {
		this.imageContent = imageContent;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getResizeWidth() {
		return resizeWidth;
	}
	public void setResizeWidth(int resizeWidth) {
		this.resizeWidth = resizeWidth;
	}
	public int getResizeHeight() {
		return resizeHeight;
	}
	public void setResizeHeight(int resizeHeight) {
		this.resizeHeight = resizeHeight;
	}
	
	
	

}
