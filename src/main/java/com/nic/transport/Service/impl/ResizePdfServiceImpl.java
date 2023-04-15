package com.nic.transport.Service.impl;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.nic.transport.Service.ResizePdfService;

@Service
public class ResizePdfServiceImpl implements ResizePdfService {

	/** The resulting PDF file. */
//public static String RESULT = "results/part4/chapter16/resized_image.pdf";
	/** The multiplication factor for the image. */
	public static float FACTOR = 0.5f;

	/**
	 * Manipulates a PDF file src with the file dest as result
	 * 
	 * @param src  the original PDF
	 * @param dest the resulting PDF
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
		PdfName key = new PdfName("ITXT_SpecialId");
		PdfName value = new PdfName("123456789");
		// Read the file
		PdfReader reader = new PdfReader(src);
		int n = reader.getXrefSize();
		//System.out.println(n);
		PdfObject object;
		PRStream stream;
		// Look for image and manipulate image stream
		for (int i = 0; i < n; i++) {
			object = reader.getPdfObject(i);
			if (object == null || !object.isStream())
				continue;
			stream = (PRStream) object;
			// if (value.equals(stream.get(key))) {
			PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
			//System.out.println(stream.type());
			if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
				PdfImageObject image = new PdfImageObject(stream);
				BufferedImage bi = image.getBufferedImage();
				if (bi == null)
					continue;
				int width = (int) (bi.getWidth() * FACTOR);
				int height = (int) (bi.getHeight() * FACTOR);
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				AffineTransform at = AffineTransform.getScaleInstance(FACTOR, FACTOR);
				Graphics2D g = img.createGraphics();
				g.drawRenderedImage(bi, at);
				ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
				ImageIO.write(img, "JPG", imgBytes);
				stream.clear();
				stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
				stream.put(PdfName.TYPE, PdfName.XOBJECT);
				stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
				stream.put(key, value);
				stream.put(PdfName.FILTER, PdfName.DCTDECODE);
				stream.put(PdfName.WIDTH, new PdfNumber(width));
				stream.put(PdfName.HEIGHT, new PdfNumber(height));
				stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
				stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
			}
		}
		// Save altered PDF
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
		stamper.setFullCompression();
		stamper.close();
		reader.close();
	}

	/**
	 * Main method.
	 *
	 * @param args no arguments needed
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		// createPdf(RESULT);
		/*
		 * new ResizeImage().manipulatePdf("C:\\Users\\Shree\\Desktop\\pdf\\10.7.pdf",
		 * "C:\\Users\\Shree\\Desktop\\MB_Photos\\Compressedjsm2.pdf");
		 */
	}
	
	/*
	 * public String base64toPdf() { BASE64Decoder decoder = new BASE64Decoder();
	 * byte[] decodedBytes = decoder.decodeBuffer(encodedBytes);
	 * 
	 * File file = new File("c:/newfile.pdf");; FileOutputStream fop = new
	 * FileOutputStream(file);
	 * 
	 * }
	 */
	
	
	/*
	 * public String getCompressedStringOutput(final String input) throws
	 * IOException { final StringInputStream inputStream = new
	 * StringInputStream(input);
	 * 
	 * final ByteArrayOutputStream out = new ByteArrayOutputStream(); final
	 * Base64OutputStream base64OutputStream = new Base64OutputStream(out);
	 * 
	 * final GZIPOutputStream gzOut = new GZIPOutputStream(base64OutputStream);
	 * final int BUFFER_SIZE = 2048; final byte[] buffer = new byte[BUFFER_SIZE];
	 * int n = 0; while (-1 != (n = inputStream.read(buffer))) { gzOut.write(buffer,
	 * 0, n); } gzOut.close(); inputStream.close(); final String compressedString =
	 * out.toString("UTF-8"); return compressedString; }
	 */
	 

}