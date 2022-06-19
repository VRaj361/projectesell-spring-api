package com.controller;


import java.io.ByteArrayInputStream;
import java.io.File; 
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bean.FileBean;
import com.google.api.client.http.InputStreamContent;

import java.util.*;


@RestController
@CrossOrigin
public class FilesController {
	
//	public String uploadFile(MultipartFile file, String filePath) {
//		  try {
//		     String folderId = getFolderId(filePath);
//		     if (null != file) {
//		        File fileMetadata = new File();
//		        fileMetadata.setParents(Collections.singletonList(folderId));
//		        fileMetadata.setName(file.getOriginalFilename());
//		        File uploadFile = googleDriveManager.getInstance()
//		              .files()
//		              .create(fileMetadata, new InputStreamContent(
//		                    file.getContentType(),
//		                    new ByteArrayInputStream(file.getBytes()))
//		              )
//		              .setFields("id").execute();
//		        return uploadFile.getId();
//		     }
//		  } catch (Exception e) {
//		     log.error("Error: ", e);
//		  }
//		  return null;
//	}
	
	
	
	

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uplpadFile(FileBean fileBean) throws IOException {
//		String message = " ";
		System.out.println(fileBean.getFile().getOriginalFilename());
		System.out.println("bytes"+fileBean.getFile().getBytes());
		
		try {

			File stadir = new File("/media/vraj/New Volume/ComputerLanguages/Spring/project-esell-api/src/main/resources/static");
			File f = new File(stadir, fileBean.getFile().getOriginalFilename());
			
			try {
				byte[] b = fileBean.getFile().getBytes();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(b);
				fo.close();
				
			} catch (Exception e) {
				System.out.println("in");
				e.printStackTrace();
			}
			
//			String filename=StringUtils.cleanPath(fileBean.getFile().getOriginalFilename());
			return Base64.getEncoder().encodeToString(fileBean.getFile().getBytes());
//			System.out.println(str.length());
			
			
			
			
			
			
//			System.out.println(fileBean.getName());
//			message = "Upload file successFully :" + fileBean.getFile().getOriginalFilename();
//			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
//			return "done"; 
		} catch (Exception e) {

//			message = "could not uplaod file..." + fileBean.getFile().getOriginalFilename();
			return "loss";
//			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
		


	}
}
