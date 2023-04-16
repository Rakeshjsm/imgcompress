package com.nic.transport.Service;

import java.io.IOException;

import com.itextpdf.text.DocumentException;

public interface ResizePdfService {

	public void manipulatePdf(String src, String dest) throws IOException, DocumentException;
	
}
