package com.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



import com.bean.FileBean;


@RestController
@CrossOrigin
public class FilesController {


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
			
//			System.out.println(fileBean.getName());
//			message = "Upload file successFully :" + fileBean.getFile().getOriginalFilename();
//			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
			return "done"; 
		} catch (Exception e) {

//			message = "could not uplaod file..." + fileBean.getFile().getOriginalFilename();
			return "loss";
//			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
		


	}
}
