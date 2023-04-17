package com.nic.transport.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;;

@JsonInclude(Include.NON_NULL)
public class ResponseBean {
	
	private String message;
	private String status;
	private Object data;
	private byte [] pdfContent;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public byte[] getPdfContent() {
		return pdfContent;
	}
	public void setPdfContent(byte[] pdfContent) {
		this.pdfContent = pdfContent;
	}
	
	
}
