package com.controller;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bean.FileBean;
import com.bean.FileDB;
import com.bean.UserBean;
import com.bean.UserBeanAuth;
import com.dao.FileDBRepository;
import com.google.api.client.http.InputStreamContent;

import java.util.*;

import javax.imageio.ImageIO;


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
	
	
	
	
	@Autowired
	JdbcTemplate j;
	@Autowired
	private FileDBRepository fileDBRepository;
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uplpadFile(FileBean fileBean,@RequestHeader("authtoken") String authtoken) throws IOException {
//		String message = " ";
//		System.out.println(fileBean.getFile().getOriginalFilename());
//		System.out.println("bytes"+fileBean.getFile().getBytes());
//		System.out.println(fileBean.getFile().getInputStream());
		
		String fileName = StringUtils.cleanPath(fileBean.getFile().getOriginalFilename());
		UserBeanAuth user=j.query("select * from usersa where authtoken=? ",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {authtoken}).get(0); 
	    FileDB FileDB = new FileDB(fileName, fileBean.getFile().getContentType(), fileBean.getFile().getBytes(),user.getUserid());
	    fileDBRepository.save(FileDB);
		
//	    FileDB file=fileDBRepository.findBy("08cb09ae-aa8d-4bd9-824e-33df1376479c").get();//get the data
		
//	    for(int i=0;i<file.getData().length;i++) {	    	
//	    	System.out.println(file.getData()[i]+" ");
//	    }
		System.out.println("filedb--->"+FileDB.getId());
		//store the value in database and return the id that it should be uploaded
	     
//		String str= Base64.getEncoder().encodeToString(file.getData());
//		System.out.println("String--->"+str);
	    return FileDB.getId();
//		return file.getId();

		
//		File f=new File("/media/vraj/New Volume/ComputerLanguages/Spring/project-esell-api/src/main/resources/static/Vraj.png");
//		System.out.println("file-->"+f);
//		FileInputStream ff=new FileInputStream(f);
//		System.out.println("fileff--->"+ff);
//		byte[] a=fileBean.getFile().getBytes();
//		for(int i=0; i< a.length ; i++) {
//	         System.out.print(a[i] +" ");
//	      }
//		ByteArrayInputStream inStreambj = new ByteArrayInputStream(a);
//        
//	      // read image from byte array
//	      BufferedImage newImage = ImageIO.read(inStreambj);
//	        
//	      // write output image
//	      ImageIO.write(newImage, "png", new File("/media/vraj/New Volume/ComputerLanguages/Spring/project-esell-api/src/main/resources/static/outputImage.jpg"));
//	      System.out.println("Image generated from the byte array.");
//		j.update("insert into files (filedata) values ?",fileBean.getFile().getBytes());
//		j.update("insert into files (filedata) values ?",fileBean.getFile().getInputStream(),fileBean.getFile().getSize());
		
		
//		try {
//
//			File stadir = new File("/media/vraj/New Volume/ComputerLanguages/Spring/project-esell-api/src/main/resources/static");
//			File f = new File(stadir, fileBean.getFile().getOriginalFilename());
//			
//			try {
//				byte[] b = fileBean.getFile().getBytes();
//				FileOutputStream fo = new FileOutputStream(f);
//				fo.write(b);
//				fo.close();
//				
//			} catch (Exception e) {
//				System.out.println("in");
//				e.printStackTrace();
//			}
//			
////			String filename=StringUtils.cleanPath(fileBean.getFile().getOriginalFilename());
//			return Base64.getEncoder().encodeToString(fileBean.getFile().getBytes());
////			System.out.println(str.length());
//			
//			
//			
//			
//			
//			
////			System.out.println(fileBean.getName());
////			message = "Upload file successFully :" + fileBean.getFile().getOriginalFilename();
////			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
////			return "done"; 
//		} catch (Exception e) {
//
////			message = "could not uplaod file..." + fileBean.getFile().getOriginalFilename();
//			return "loss";
////			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
//		}
		
	}
}
