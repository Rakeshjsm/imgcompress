package com.nic.transport.Service;

import java.io.File;
import java.io.IOException;

import com.itextpdf.text.DocumentException;

public class Main {
	
	private static Console console = new Console();
	/**
	* class Main PDFC (PdfCompressor)
	* @param args
	*/
	public static void main(String[] args){
		
		/* if(args.length == 2) { */
			  Title.setTitle(" PDF COMPRESSOR "); 
			  //String inputPdfFile = checkInputFile(args[0]); 
			  //String outputPdfFile = args[1];
		  
			  String inputPdoutputPdfFilefFile = "C:\\Users\\Shree\\Desktop\\1.pdf";
			  String outputPdfFile = "C:\\Users\\Shree\\Desktop\\MB_Photos\\Compressed9.pdf";
			  
		  System.out.println();
			/*
			 * Loading loading = new Loading("Compressing", LoadingType.POINT);
			 * loading.start();
			 */
		  try {
		  
		  PdfCompressor pdfCompressor = new PdfCompressor();
		  pdfCompressor.setInputFile(inputPdoutputPdfFilefFile);
		  pdfCompressor.setOutputFile(outputPdfFile); 
		  pdfCompressor.compress();
		  
		  
		  } catch (IOException | DocumentException e){ //Handle exception
			  System.out.println(); 
			  console.printError(e.toString());
		  } //stop loading
		  //loading.setRunning = false; System.out.println(); //check if the file exists, then the process is successful 
		  
			if (fileExists(outputPdfFile)) {
				System.out.println();
				console.printSucces("Original size    : \033[48;5;192;38;5;27m  "
						+ FileSize.getSizeFile(new File(inputPdoutputPdfFilefFile)) + "  \033[0m");
				console.printSucces("Compressed size  : \033[48;5;192;38;5;27m  "
						+ FileSize.getSizeFile(new File(outputPdfFile)) + "  \033[0m");
				System.out.println();
			}
		/*} else {
			Title.setTitle(" PDF COMPRESSOR ");

			System.out.println("created by Hendriyawan");
			System.out.println();
			console.printError("Usage : pdfc <inputfile> <outputfile> \n");
			System.exit(1);
		}*/
		 
	}
	
	/*PDFC (Pdf Compressor)
	* @param inputFile
	*/
	public static String checkInputFile(String inputFile){
		String file = "";
		File target = new File(inputFile);
		//check if exists
		if(target.exists()){
			file = inputFile;
		}
		else {
			System.out.println();
			console.printError("No such file or directory : "+inputFile+"\n");
			System.exit(1);
		}
		return file;
	}
	
	/*PDFC (Pdf Compressor)
	* @param outputFile
	*/
	public static boolean fileExists(String outputFile){
		boolean exists = false;
		File file = new File(outputFile);
		if(file.exists()){
			exists = true;
		}
		return exists;
	}
}
