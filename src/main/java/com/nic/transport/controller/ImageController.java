package com.nic.transport.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nic.transport.dto.RequestUpImg;
import com.nic.transport.dto.ResponseBean;
import com.nic.transport.util.CommonUtils;
import com.nic.transport.util.ImageResizeSettings;

@RestController
public class ImageController {
	
	@Value("${FILE_UPLOADED_PATH}")
	private String uploaedPath;
	
	@Value("${FILE_COMPRESSED_PATH}")
	private String compressedPath;

	@Value("${IMAGE_COMPRESSION_RESIZE}")
	private String data;
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
			// 
			convFile.get
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
		response.setStatus("SUCCESS");
		//response = imgValidateReq(file);
		if (response.getStatus().equals("FAIL")) {
			return response;
		}
		System.out.println("Before base 64");
		System.out.println(requestUpImg.getImageContent().toString());
		//requestUpImg.setImageContent(base64StringtoByteArray(requestUpImg.getImageContent()));
		System.out.println("After base 64"); 
		System.out.println(requestUpImg.getImageContent());

		
		String newCompressedFileName = compressedPath + UUID.randomUUID()
				 + requestUpImg.getFileName();

		File JPGRE_SIZE = new File(newCompressedFileName);

		try {
			File convFile = convertFileByteArray(requestUpImg);
			System.out.println();
			String extension = requestUpImg.getFileName().split("\\.")[1];
			CommonUtils.getImageResizeSettings(data);
			int sizeInMB = getSizeOfImage(requestUpImg.getImageContent().length);
			ImageResizeSettings imageResizeSettings = CommonUtils.getImageResizeSettings(sizeInMB);
			BufferedImage resize = resizeImage(convFile, JPGRE_SIZE, imageResizeSettings.getResizeWidth(), imageResizeSettings.getResizeHeight(), "jpg");
			byte[] bytearray = fileToBase64StringConversion( resize, extension);

			response.setData(bytearray);
			response.setStatus("SUCCESS");
			response.setMessage("SUCCESS");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;

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
		+FilenameUtils.getBaseName(file.getOriginalFilename()) +"."
		+FilenameUtils.getExtension(file.getOriginalFilename());
		
		File convFile = new File(newUploadedFileName);
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		
		return convFile;
	}

	public File convertFileByteArray(RequestUpImg requestUpImg) throws IOException {

		String newUploadedFileName = uploaedPath + UUID.randomUUID() + requestUpImg.getFileName();
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
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(resize, type, baos );
		byte[] imageInByte=baos.toByteArray();
		return imageInByte;
		
	}
	
	private byte[] base64StringtoByteArray(byte [] data) throws UnsupportedEncodingException {
		 byte[] decodedString = Base64.getDecoder().decode(new String(data).getBytes("UTF-8"));
		 return decodedString;
         
	}
	
	private int getSizeOfImage(long length) {
		int size=1;
		long byteInMB= 1024*1024*1024;
		if(length < byteInMB)
		{
			size = 1;
		}else if(length > byteInMB && length < byteInMB*2)
		{
			size = 2;
		}else if(length > byteInMB*2 && length < byteInMB*3)
		{
			size = 3;
		}else if(length > byteInMB*3 && length < byteInMB*4)
		{
			size = 4;
		}else if(length > byteInMB*4 && length < byteInMB*5)
		{
			size = 5;
		}else if(length > byteInMB && length < byteInMB*2)
		{
			size = 2;
		}else if(length > byteInMB && length < byteInMB*2)
		{
			size = 2;
		}else if(length > byteInMB && length < byteInMB*2)
		{
			size = 2;
		}
		
		return size;
	}
	

}
