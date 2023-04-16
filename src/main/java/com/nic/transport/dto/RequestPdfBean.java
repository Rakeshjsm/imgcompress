package com.nic.transport.dto;

public class RequestPdfBean {
	

	private String source;
	private String dest;
	
	private byte [] pdfContent;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public byte[] getPdfContent() {
		return pdfContent;
	}
	public void setPdfContent(byte[] pdfContent) {
		this.pdfContent = pdfContent;
	}
	
	
	
}
