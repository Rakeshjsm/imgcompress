package com.nic.transport.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.catalina.connector.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nic.transport.Service.ResizePdfService;
import com.nic.transport.dto.RequestPdfBean;
import com.nic.transport.dto.RequestUpImg;
import com.nic.transport.dto.ResponseBean;
import com.nic.transport.util.CommonUtils;
import com.nic.transport.util.ImageResizeSettings;

@RestController
public class ImageController {

	@Autowired
	private ResizePdfService resizePdf;

	@Value("${IMAGE_COMPRESSION_RESIZE}")
	private String data;

	public static final String tmpdir = System.getProperty("java.io.tmpdir");
	public static String uploaedPath = "";
	public static String compressedPath = "";

	static {
		File upload = new File(tmpdir + "uploadFile/");
		if (!upload.exists()) {
			upload.mkdir();
		}

		uploaedPath = upload.getPath() + "/";

		File compress = new File(tmpdir + "compress");
		if (!compress.exists()) {
			compress.mkdir();
		}

		compressedPath = compress.getPath() + "/";
		System.out.println(compressedPath);
	}

	@GetMapping("/hello")
	public ArrayList<ImageResizeSettings> hello() {
		return CommonUtils.getImageResizeSettings(data);
	}

	@PostMapping("/uploadImage")
	public ResponseBean uploadimage(@RequestParam("file") MultipartFile file) throws Exception {
		ResponseBean response = new ResponseBean();

		response = imgValidateReq(file);
		if (response.getStatus().equals("FAIL")) {
			return response;
		}

		String extension = FilenameUtils.getExtension(file.getOriginalFilename());

		String newCompressedFileName = compressedPath + UUID.randomUUID()
				+ FilenameUtils.getBaseName(file.getOriginalFilename()) + "." + extension;

		File JPGRE_SIZE = new File(newCompressedFileName);

		try {
			File convFile = convert(file);
			BufferedImage resize = resizeImage(convFile, JPGRE_SIZE, 800, 800, "jpg");
			byte[] bytearray = fileToBase64StringConversion(resize, extension);

			response.setData(bytearray);
			response.setStatus("SUCCESS");
			response.setMessage("SUCCESS");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;

	}

	/*
	 http://localhost:8080/uploadImagebase64
	 
	 {
"imageContent" : "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",
"fileName"  : "abc.jpg",
"resizeWidth" : 100,
"resizeHeight" : 100
}
	 * */
	
	@PostMapping("/uploadImagebase64")
	public ResponseBean uploadimageBase64(@RequestBody RequestUpImg  requestUpImg) throws Exception {
		ResponseBean response = new ResponseBean();
		
		if(requestUpImg.getImageContent() == null || requestUpImg.getImageContent().length == 0) {
			response.setStatus("FAIL");
			response.setMessage("Error! imageContent must be required.");
			return response;
		}
		
			
		int sizeInBytes = requestUpImg.getImageContent().length;
			if(sizeInBytes < 1048576){ response.setStatus("FAIL");
			response.setMessage("Error! File size must be greater than 1 MB."); return
			response; }
		 
		
				
		String fileExt = getFileExtention(requestUpImg.getImageContent()); 
		if (fileExt == null || !"'.jpg','.png','.JPG','.PNG','.jpeg','.JPEG', '.gif', .GIF', '.svg', '.SVG'".contains(fileExt)) {
			response.setMessage("Upload file extention must be .JPEG, .PNG, .SVG,.GIF");
			response.setStatus("FAIL");
			return response;
		}
		
		//System.out.println("Before base 64");
		//System.out.println(requestUpImg.getImageContent().toString());
		//requestUpImg.setImageContent(base64StringtoByteArray(requestUpImg.getImageContent()));
		//System.out.println("After base 64"); 
		//System.out.println(requestUpImg.getImageContent());

		String fileName = "imgComp."+fileExt; 
		String newCompressedFileName = compressedPath + UUID.randomUUID()
				 + fileName;
		System.out.println("newCompressedFileName-----"+newCompressedFileName);
		File JPGRE_SIZE = new File(newCompressedFileName);
		
		try {
			File convFile = convertFileByteArray(requestUpImg, fileName);
			if (!isValidImage(convFile)) {
				response.setMessage("file currupted.");
				response.setStatus("FAIL");
				return response;
			}
			
			//String extension = requestUpImg.getFileName().split("\\.")[1];
			CommonUtils.getImageResizeSettings(data);
			int sizeInMB = getSizeOfImage(requestUpImg.getImageContent().length);
			//System.out.println( sizeInMB + " sizeInMB ");
			//System.out.println(requestUpImg.getImageContent().length);
			ImageResizeSettings imageResizeSettings = CommonUtils.getImageResizeSettings(sizeInMB);
			//System.out.println(imageResizeSettings);
			BufferedImage resize = resizeImage(convFile, JPGRE_SIZE, imageResizeSettings.getResizeWidth(), imageResizeSettings.getResizeHeight(), fileExt);
			byte[] bytearray = fileToBase64StringConversion( resize, fileExt);

			response.setData(bytearray);
			response.setStatus("SUCCESS");
			response.setMessage("SUCCESS");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;

	}

	private static String getFileExtention(byte[] imageContent) {

		InputStream is = new ByteArrayInputStream(imageContent);

		String mimeType = null;
		String fileExtension = null;
		try {
			mimeType = URLConnection.guessContentTypeFromStream(is); // mimeType is something like "image/jpeg"
			if (mimeType == null) {
				return mimeType;
			}
			String delimiter = "[/]";
			String[] tokens = mimeType.split(delimiter);
			fileExtension = tokens[1];
		} catch (IOException ioE) {
			ioE.printStackTrace();
		}
		return fileExtension;
	}

	private static BufferedImage resizeImage(File originalfile, File resizeImage, int width, int height,
			String format) {
		BufferedImage resize = null;
		try {
			BufferedImage original = ImageIO.read(originalfile);
			resize = new BufferedImage(width, height, original.getType());
			Graphics2D g2 = resize.createGraphics();
			g2.drawImage(original, 0, 0, width, height, null);
			ImageIO.write(resize, format, resizeImage);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return resize;
	}

	public File convert(MultipartFile file) throws IOException {

		String newUploadedFileName = uploaedPath + UUID.randomUUID()
				+ FilenameUtils.getBaseName(file.getOriginalFilename()) + "."
				+ FilenameUtils.getExtension(file.getOriginalFilename());

		File convFile = new File(newUploadedFileName);
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		return convFile;
	}

	public File convertFileByteArray(RequestUpImg requestUpImg, String fileName) throws IOException {

		String newUploadedFileName = uploaedPath + UUID.randomUUID() + fileName;
		System.out.println("uploaedPath ====" + newUploadedFileName);
		File convFile = new File(newUploadedFileName);
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(requestUpImg.getImageContent());
		fos.close();
		return convFile;
	}

	private ResponseBean imgValidateReq(MultipartFile file) {
		ResponseBean response = new ResponseBean();
		if (file == null) {
			response.setMessage("Upload file must be reqired.");
			response.setStatus("FAIL");
			return response;
		}

		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!"'jpg','png','JPG','PNG','jpeg','JPEG'".contains(extension)) {
			response.setMessage("Upload file extention must be .JPEG, .PNG ! file Type is " + file.getContentType());
			response.setStatus("FAIL");
			return response;
		}

		if (file.getSize() > 10485760) {
			response.setMessage("Uploaded file is too large. Please upload max of 10 MB size ");
			response.setStatus("FAIL");
			return response;
		}
		if (file.getSize() < 20480) {
			response.setMessage("Uploaded file is too small. Please upload min of 20 KB size ");
			response.setStatus("FAIL");
			return response;
		}

		response.setStatus("SUCCESS");
		return response;
	}

	public byte[] fileToBase64StringConversion(BufferedImage resize, String type) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resize, type, baos);
		byte[] imageInByte = baos.toByteArray();
		return imageInByte;

	}

	private byte[] base64StringtoByteArray(byte[] data) throws UnsupportedEncodingException {
		byte[] decodedString = Base64.getDecoder().decode(new String(data).getBytes("UTF-8"));
		return decodedString;

	}

	private int getSizeOfImage(long length) {
		int size = 1;
		long byteInMB = 1024 * 1024;
		if (length < byteInMB) {
			size = 1;

		} else if (length > byteInMB && length < byteInMB * 2) {
			size = 2;
		} else if (length > byteInMB * 2 && length < byteInMB * 3) {
			size = 3;
		} else if (length > byteInMB * 3 && length < byteInMB * 4) {
			size = 4;
		} else if (length > byteInMB * 4 && length < byteInMB * 5) {
			size = 5;
		} else if (length > byteInMB * 5 && length < byteInMB * 6) {
			size = 6;
		} else if (length > byteInMB * 6 && length < byteInMB * 7) {
			size = 7;
		} else if (length > byteInMB * 7 && length < byteInMB * 8) {
			size = 8;
		} else if (length > byteInMB * 8 && length < byteInMB * 9) {
			size = 9;
		} else if (length > byteInMB * 9 && length < byteInMB * 10) {
			size = 9;
		} else if (length > byteInMB * 10 && length < byteInMB * 10) {
			size = 10;
		}
		return size;
	}

	@PostMapping("/compressPdf")
	public ResponseBean uploadPDFbase64(@RequestBody RequestPdfBean req) throws Exception {
		ResponseBean response = new ResponseBean();
		resizePdf.manipulatePdf(req.getSource(), req.getDest());
		response.setMessage("pdfcompressed");
		return response;

	}

	private boolean isValidImage(File f) {
		boolean isValid = true;
		try {
			ImageIO.read(f).flush();
		} catch (Exception e) {
			isValid = false;
		}
		return isValid;
	}
}