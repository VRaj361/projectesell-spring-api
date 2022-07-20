package com.controller;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bean.ResponceUserBean;
import com.bean.ResponceUserBeanAuth;
import com.bean.UserBean;
import com.bean.UserBeanAuth;
import com.dao.UserDao;
import com.services.EmailService;
import com.services.GenerateToken;

@RestController
@CrossOrigin
public class UserController {
	@Autowired
	UserDao userDao;
	
	@Autowired
	EmailService email_service;
	
	
	@PostMapping("/user")
	public UserBean addUser(@RequestBody UserBean user) {
		userDao.addUser(user);
		return user;
	}
	
	@GetMapping("/user")
	public List<UserBean> getAllUser(){
		return userDao.getAllUser();
	}
	
	@PutMapping("/user")
	public UserBean updateUser(@RequestBody UserBean user) {
		userDao.updateUser(user);
		return user;
	}
	

	@PostMapping("/sendemailu")
	 public boolean sendEmailOfPassword(@RequestBody UserBean user) throws AddressException, MessagingException, IOException {
	  //call the email method in main service method
	  UserBean user1=userDao.particularUSer(user.getUserid());
	  return email_service.sendPassword(user1.getEmail(),user1.getPassword());

	 }
	

	@PostMapping("/sendemail")
	public String checkSendEmail(@RequestBody UserBean user) {
		System.out.println("email -> recived "+user.getEmail());
		Random rnd = new Random();
		int number = rnd.nextInt(999999);
		
		// this will convert any number sequence into 6 character.
		String otp = String.format("%06d", number);
		System.out.println(otp);
	
		try {
			
			email_service.sendOtp(user.getEmail(), otp);
		}catch(Exception e) {
			e.printStackTrace();
			return "-1";
		}
		System.out.println("Email send Successfully--------->");
		int userid=userDao.particularUser(user.getEmail());
		return otp+" "+userid;
	}
	
	
	
	
	
	
//	@PostMapping("/signupcusres")
//	public ResponseEntity<?> addUserCusRes(@RequestBody UserBean bean){
//		UserBean duplicate=userDao.findUserAuth(bean.getEmail());
////		ResponceUserBean<UserBean> res=new ResponceUserBean<>();
//		if(duplicate==null) {
//			return ResponseEntity.ok(bean);
//		}else {
//			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED );
//		}
//	}
	
//	@PostMapping("/signupcus")
//	public ResponceUserBean<?> addUserCus(@RequestBody UserBean bean){
//		UserBean duplicate=userDao.findUser(bean.getEmail());
//		ResponceUserBean<UserBean> res=new ResponceUserBean<>();
//		if(duplicate==null) {
//			res.setData(bean);
//			res.setMsg("Signup Successfully");
//			res.setStatus(200);
//		}else {
//			res.setData(null);
//			res.setStatus(-1);
//			res.setMsg("Email is already Exists");
//		}
//		return res;
//	}
	
	//for authtoken use table usersauth
	
	
	@PostMapping("/signupcus")
	public ResponceUserBeanAuth<?> addUserCus(@RequestBody UserBeanAuth bean){
		UserBeanAuth duplicate=userDao.findUser(bean);
		
		ResponceUserBeanAuth<UserBeanAuth> res=new ResponceUserBeanAuth<>();
		if(duplicate==null) {
			GenerateToken gen=new GenerateToken();
			String str=gen.generateToken(10);
			userDao.setToken(bean.getEmail(),str);
			bean.setAuthtoken(str);
			res.setData(bean);
			res.setMsg("Signup Successfully");
			res.setStatus(200);
		}else {
			res.setData(null);
			res.setStatus(400);
			res.setMsg("Email is already Exists");
		}
		return res;
	}
	
	//update record (first check key and after get the record)
	@PutMapping("/updatecus")
	public ResponceUserBeanAuth<?> updateUserCus(@RequestBody UserBeanAuth bean){
		System.out.println("data are there --->"+bean.getAuthtoken()+" "+bean.getPassword());
		ResponceUserBeanAuth<UserBeanAuth> res=new ResponceUserBeanAuth<>();
		UserBeanAuth user = userDao.findKey(bean);
		if(user != null) {
			UserBeanAuth userResp = userDao.updateUserCus(bean);
			GenerateToken gen=new GenerateToken();
			String str=gen.generateToken(10);
			userDao.setToken(userResp.getEmail(),str);
			userResp.setAuthtoken(str);
			res.setData(userResp);
			res.setStatus(200);
			res.setMsg("Update Successfully");
		}else {
			res.setData(null);
			res.setStatus(404);
			res.setMsg("No User found");
		}
		return res;
	}
	
	
	@GetMapping("/userauth")
	public ResponseEntity<?> getAllUsreAuth(){
//		ResponceUserBeanAuth<UserBeanAuth> res=new ResponceUserBeanAuth<>();
		List<UserBeanAuth> users=userDao.getAllUserAuth("yc4McUkus3");
		if(users.size()==0) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}else {
			return ResponseEntity.ok(users);
		}
	}

	
	
	
	
	
}
