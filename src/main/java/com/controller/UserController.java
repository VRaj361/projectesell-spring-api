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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.dao.UserDao;
import com.services.EmailService;
import com.services.GenerateToken;
import com.bean.*;

@RestController
@CrossOrigin
public class UserController {
	@Autowired
	UserDao userDao;
	
	@Autowired
	EmailService email_service;
	
	@Autowired
	BCryptPasswordEncoder bcypt;
	
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
	
	
	
	//before send email for otp
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
	
	
	
	/*
	 * 
	 * 
	 * Authenticate API's
	 * 
	 * 
	 */
	
	
	
//	@PostMapping("/signupcusres")
//	public ResponseEntity<?> addUserCusRes(@RequestBody UserBean bean){
//		UserBean duplicate=userDao.findUserAuth(bean.getEmail());
//		if(duplicate==null) {
//			return ResponseEntity.ok(bean);
//		}else {
//			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED );
//		}
//	}
	
	
	//login ,signup, updateuser all api can change authtoken every call
	//duplicate user found 
	//for authtoken use table usersauth
	@PostMapping("/signupcus")
	public ResponceUserBeanAuth<?> addUserCus(@RequestBody UserBeanAuth bean){
		String pass_enc=bcypt.encode(bean.getPassword());
		bean.setPassword(pass_enc);
		UserBeanAuth duplicate=userDao.findUser(bean);
		ResponceUserBeanAuth<UserBeanAuth> res=new ResponceUserBeanAuth<>();
		if(duplicate==null) {
			GenerateToken gen=new GenerateToken();
			String str=gen.generateToken(10);
			userDao.setTokenE(bean.getEmail(),str);
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
		ResponceUserBeanAuth<UserBeanAuth> res=new ResponceUserBeanAuth<>();
		
		UserBeanAuth user = userDao.findKey(bean);
		if(user != null) {
			UserBeanAuth userResp = userDao.updateUserCus(bean);
			
			if(userResp == null) {
				res.setData(null);
				res.setStatus(401);
				res.setMsg("Incorrect Data");
			}
			GenerateToken gen=new GenerateToken();
			String str=gen.generateToken(10);
			userDao.setTokenA(bean.getAuthtoken(),str);
			userResp.setAuthtoken(str);
			res.setData(userResp);
			res.setStatus(200);
			res.setMsg("Update Successfully");
		}else {
			res.setData(null);
			res.setStatus(404);
			res.setMsg("User does not exists");
		}
		return res;
	}
	
	//login user to get return his record
	//first check authtoken and after check data 
	//authtoken is wrong then user does not exists
	@PostMapping("/logincus")
	public ResponceUserBeanAuth<?> loginUserCus(@RequestBody UserBeanAuth bean){
		ResponceUserBeanAuth<UserBeanAuth> res = new ResponceUserBeanAuth<>();
		UserBeanAuth user = userDao.findUserLogin(bean);
		if(user != null ) {
			GenerateToken gen=new GenerateToken();
			String str=gen.generateToken(10);
			userDao.setTokenE(user.getEmail(),str);
			user.setAuthtoken(str);
			res.setData(user);
			res.setStatus(200);
			res.setMsg("Login Successfully");
		}else {
			res.setData(null);
			res.setStatus(404);
			res.setMsg("User does not exists");
		}
		return res;
	}
	
	
	//get all data for particular user using authtoken
	@GetMapping("/getuserdata")
	public ResponceUserBeanAuth<?> getUserData(@RequestHeader("authtoken") String authtoken){
		ResponceUserBeanAuth<UserBeanAuth> res_user=new ResponceUserBeanAuth<UserBeanAuth>();
		UserBeanAuth user= userDao.getUserData(authtoken);
		if(user ==null) {
			res_user.setData(null);
			res_user.setMsg("User Does not Exists");
			res_user.setStatus(404);
		}else {
			res_user.setData(user);
			res_user.setMsg("Getting Data Successfully");
			res_user.setStatus(200);
		}
		return res_user;
	}
	
	//check the data of user
	@PostMapping("/checkuserdata")
	public ResponceUserBeanAuth<?> checkUserData(@RequestBody UserBeanAuth bean){
		ResponceUserBeanAuth<UserBeanAuth> res_user=new ResponceUserBeanAuth<UserBeanAuth>();
		UserBeanAuth user = userDao.checkUserData(bean);
		if(user ==null) {
			res_user.setData(null);
			res_user.setMsg("User Does not Exists");
			res_user.setStatus(404);
		}else {
			res_user.setData(user);
			res_user.setMsg("Getting Data Successfully");
			res_user.setStatus(200);
		}
		return res_user;
	}
	
	
	//get token (use for login)
	@GetMapping("/getanytoken")
	public String getAnyToken() {
		return userDao.getAnyToken();
	}
	
//  get user (first check by token after getting all record)
//	@GetMapping("/userauth")
//	public ResponseEntity<?> getAllUsreAuth(@RequestBody UserBeanAuth bean){
//		List<UserBeanAuth> users=userDao.getAllUserAuth(bean.getAuthtoken());
//		if(users.size()==0) {
//			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//		}else {
//			return ResponseEntity.ok(users);
//		}
//	}
	
	//get user (first check by token after getting all record)
	//provide random authtoken for access record
	@GetMapping("/userauth")
	public ResponceUserBeanAuth<List<UserBeanAuth>> getAllUserAuth(@RequestHeader("authToken") String authToken){
		ResponceUserBeanAuth<List<UserBeanAuth>> res = new ResponceUserBeanAuth<>();
		List<UserBeanAuth> users = userDao.getAllUserAuth(authToken);
		if(users != null) {
			res.setData(users);
			res.setStatus(200);
			res.setMsg("Login Successfully");
		}else {
			res.setData(null);
			res.setStatus(401);
			res.setMsg("Unauthorized");
		}
		return res;
	}
	
	//sending mail and getting authenticated otp to check user otp
	@PostMapping("/otpemail")
	public String sendEmailForOTP(@RequestBody UserBeanAuth user) {
		String check = userDao.checkUser(user);
		if(check!=null||check!="") {
			Random rnd = new Random();
			int number = rnd.nextInt(999999);
			String otp = String.format("%06d", number);
			try {
				email_service.sendOtp(user.getEmail(), otp);
				//encryption in otp
				String str=bcypt.encode(otp);
				
				return check+","+bcypt.encode(otp);//save as front end cookie
			}catch(Exception e) {
				e.printStackTrace();
				return "-1";
			}
		}else {
			return "-1";
		}
	}
	
	//check password using decryption algorithm
	@PostMapping("/otpemailcheck")
	public boolean checkOtpEmail(@RequestBody UserBeanAuth user) {
//		boolean check = userDao.checkUser(user); condition check&&
		if( user.getOtp().length()>8) {
			//decrypt
			//client side set otp and cookie otp as comma seprated
			String[] otps = user.getOtp().split(",");
			if(bcypt.matches(otps[0], otps[1])) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	//add review for logged in user
	@PostMapping("/addreview")
	public ResponceUserBeanAuth<?> addReviewParUser(@RequestBody ReviewBean review){
		ResponceUserBeanAuth<ReviewBean> res = new ResponceUserBeanAuth<>();
		ReviewBean rev = userDao.postAddReview(review);
		if(rev != null) {
			res.setData(rev);
			res.setStatus(200);
			res.setMsg("Add Review Successfully");
		}else {
			res.setData(null);
			res.setStatus(401);
			res.setMsg("Unauthorized");
		}
		return res; 
			
	}
	//get all revies
	@GetMapping("/getreviews")
	public List<ReviewBean> getreviews(){
		return userDao.getreviews();
	}
	
	
	
	//Add Auction user
	@PostMapping("/addauction")
	public AuctionBean addAuction(@RequestBody AuctionBean auction) {
		userDao.addAuction(auction);
		return auction;
	}
}
