package com.nic.transport.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nic.transport.dto.ResponseBean;

@RestController
public class ImageController {
	
	//static value move to the properties file
	
	//validation minimum dimention max dimention for uploded doc.,  dimention max dimention for new resulation
	
	//multipart ke badle byte arr version bhi banana hai 
	
	//API response allwase will be json
	
	
	@Value("${RESPONSE_FILE_PATH}")
	private String path;
	
	@PostMapping("/uploadImage") 
	public ResponseBean uploadimage(@RequestParam("file") MultipartFile file) throws Exception {
		ResponseBean response = new ResponseBean(); 
		
		
		System.out.println(file.getContentType());
		System.out.println(file.getName());
		System.out.println(file.getOriginalFilename());
		System.out.println(file.getSize());
		
		response = imgValidateReq(file);
		if(response.getStatus().equals("FAIL")) {
			return response;
		}
		
		
		
		/*
		 * File JPGRE_SIZE = new File(path+(new Date()).getTime()+".jpg"); File convFile
		 * = convert(file);
		 * 
		 * resizeImage(convFile, JPGRE_SIZE , 300, 300, "jpg");
		 */
		
		
		return response;

	}

	private static void resizeImage(File originalfile, File resizeImage, int width, int height, String format) {
		try {

			BufferedImage original = ImageIO.read(originalfile);
		 	BufferedImage resize = new BufferedImage(width, height, original.getType());
			Graphics2D g2 = resize.createGraphics();
			g2.drawImage(original, 0, 0, width, height, null);
			ImageIO.write(resize, format, resizeImage);

		}

		catch (IOException ex) {
			ex.printStackTrace();

		}

	}
	
	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
	
	
	private ResponseBean imgValidateReq(MultipartFile file) {
		ResponseBean response = new ResponseBean();
		if(file == null) {
			response.setMessage("Upload file must be reqired.");
			response.setStatus("FAIL");
			return response;
		}
		
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!"'jpg','png',".contains(extension)) {
			response.setMessage("Upload file extention must be .JPEG, .PNG ! file Type is "+file.getContentType());
			response.setStatus("FAIL");
			return response;
		}
		
		if (file.getSize() > 1048576  || file.getSize() < 199000 ) {
			response.setMessage("Upload file size must be min 199 KB and max 10 MB ");
			response.setStatus("FAIL");
			return response;
		}
		
		response.setStatus("SUCCESS");
		return response;
	}
		

	
}

