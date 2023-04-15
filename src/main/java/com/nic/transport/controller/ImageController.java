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