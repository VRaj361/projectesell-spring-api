package com.dao;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.bean.UserBean;
import com.bean.UserBeanAuth;
import com.bean.AuctionBean;
import com.bean.FileDB;
import com.bean.FileDBA;
import com.bean.ProductBean;
import com.bean.ReviewBean;
import com.google.api.services.drive.model.User;

@Repository
public class UserDao {
	
	@Autowired
	JdbcTemplate st;
	
	@Autowired
	BCryptPasswordEncoder bcypt;
	
	@Autowired
	private FileDBARepository fileDBARepository;
	
	public void addUser(UserBean user) {
		st.update("insert into users (firstname,lastname,createdate,gender,email,password,phonenum,roleid,address) values (?,?,?,?,?,?,?,2,'')",user.getFirstname(),user.getLastname(),user.getCreatedate(),user.getGender(),user.getEmail(),user.getPassword(),user.getPhonenum());		
	}//post request
	
	public List<UserBean> getAllUser(){
		return st.query("select * from users",new BeanPropertyRowMapper<UserBean>(UserBean.class));
	}//get request
	
	public void updateUser(UserBean user) {
		st.update("update users set firstname=?,lastname=?,email=?,password=?,phonenum=?,address=? where userid=?",user.getFirstname(),user.getLastname(),user.getEmail(),user.getPassword(),user.getPhonenum(),user.getAddress(),user.getUserid());
	}//update request

	
	public int particularUser(String email) {
		List<UserBean> users=st.query("select userid from users where email = ?", new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[] {email});
		return users.get(0).getUserid();
	}
	
	
	 public UserBean particularUSer(int userid) {
	  List<UserBean> users=st.query("select * from users where userid = ?", new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[] {userid});
	  return users.get(0);
	 }//method done for getting password
	
	/*
	 * 
	 * Authenticate API's
	 * 
	 * 
	 */
	
	//user find   (return null or user) -> check for duplication
	public UserBeanAuth findUser(UserBeanAuth user) {
		List<UserBeanAuth> users=st.query("select * from usersa where email = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getEmail()});
		if(users.size()==0) {
			st.update("insert into usersa (firstname,lastname,createdate,gender,email,password,phonenum,address) values (?,?,?,?,?,?,?,?)",user.getFirstname(),user.getLastname(),user.getCreatedate(),user.getGender(),user.getEmail(),user.getPassword(),user.getPhonenum(),user.getAddress());
			return null;//user does not exists
		}else {
			return users.get(0);//one user can exists
		}
	}
	
	//user find for login
	public UserBeanAuth findUserLogin(UserBeanAuth user) {
		List<UserBeanAuth> users=st.query("select * from usersa where authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getAuthtoken()});
		if(users.size()==0) {
			return null;//user does not exists
		}else {
			List<UserBeanAuth> loginUser=st.query("select * from usersa where email = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getEmail()});
			if(loginUser == null ||loginUser.size() == 0) {
				return null;
			}else {
				if(bcypt.matches(user.getPassword(), loginUser.get(0).getPassword())) {
					return loginUser.get(0);
				}else {
					return null;
				}
			}
		}
	}
	
	//return particular user based on authtoken
	public UserBeanAuth getUserData(String authtoken) {
		List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ?",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {authtoken});
		if(user == null) {
			return null;
		}else {
			return user.get(0);
		}
	}
	
	
	//get token from the database
	public String getAnyToken() {
		List<UserBeanAuth> user= st.query("select authtoken from usersa order by random() limit 1", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {});
		return user.get(0).getAuthtoken();
	}
	
	//check authtoken first for update data 
	public UserBeanAuth findKey(UserBeanAuth user) {
		List<UserBeanAuth> users = st.query("select * from usersa where authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getAuthtoken()});
		if(users.size()==1) {
			return users.get(0);
		}else {
			return null;
		}
	}
	
	//updatedata according condition
	public UserBeanAuth updateUserCus(UserBeanAuth user) {
		if(user.getPassword()!=null) {
			st.update("update usersa set password=? where authtoken=?",bcypt.encode(user.getPassword()),user.getAuthtoken());
		}else if(user.getFirstname()!=null && user.getLastname()!=null && user.getPhonenum()!=null) {
			st.update("update usersa set firstname=?,lastname=?,phonenum=? where authtoken=?",user.getFirstname(),user.getLastname(),user.getPhonenum(),user.getAuthtoken());
		}else if(user.getAddress()!=null) {
			st.update("update usersa set address=? where authtoken=?",user.getAddress(),user.getAuthtoken());
		}else {
			return null;
		}
		return st.query("select * from usersa where authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getAuthtoken()}).get(0);
	}
	
	//checking data for user using auth and other data
	public UserBeanAuth checkUserData(UserBeanAuth user) {
		if(user.getPassword()!=null) {
			List<UserBeanAuth> req_user = st.query("select * from usersa where authtoken=?",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getAuthtoken()});
			if(req_user == null || req_user.size() == 0) {
				return null;
			}else {
				if(bcypt.matches(user.getPassword(), req_user.get(0).getPassword())) {
					return req_user.get(0);
				}else {
					return null;
				}
			}
		}else {
			return null;
		}
	}
	
	//get user using token(login)
	public List<UserBeanAuth> getAllUserAuth(String token){
		List<UserBeanAuth> user=st.query("select * from usersa where authtoken=?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {token});
		if(user.size() == 0) {
			return null;
		}else {
			return st.query("select * from usersa", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {});
		}
	}

	//set token in database(signup)
	public void setTokenA(String authtoken,String str) {
		st.update("update usersa set authtoken = ? where authtoken=?",str,authtoken);
	}
	public void setTokenE(String email,String str) {
		st.update("update usersa set authtoken = ? where email=?",str,email);
	}
	
	//check user for send email
	public String checkUser(UserBeanAuth user) {
		List<UserBeanAuth> users = st.query("select * from usersa where authtoken = ?  ", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getAuthtoken()});
		
		if(users.size()==0 || users == null) {
			return null;
		}else {
			List<UserBeanAuth> bean = st.query("select * from usersa where email = ?  ", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getEmail()});
			if(bean.size()==0 || bean == null) {
				return null;
			}else {
				return bean.get(0).getAuthtoken();
			}
		}
	}
	
	//add review
	public ReviewBean postAddReview(ReviewBean bean) {
		List<UserBeanAuth> user=st.query("select * from usersa where authtoken = ?",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {bean.getAuthtoken()});
		if(user.size()==0 || user == null) {
			return null;
		}else {
			st.update("insert into reviews (name,title,description,keywords,date,userid) values (?,?,?,?,?,?)",bean.getName(),bean.getTitle(),bean.getDescription(),bean.getKeywords(),bean.getDate(),user.get(0).getUserid());
		}
		bean.setUserid(user.get(0).getUserid());
		return bean;
	}
	
	public List<ReviewBean> getreviews(){
		return st.query("select * from reviews",new BeanPropertyRowMapper<ReviewBean>(ReviewBean.class));
	}
	
	// add auction
	public void addAuction(AuctionBean auction) {
		st.update("insert into auctions (username,bid,rangelowbid,rangehighbid,productname,category,description,ageproduct,time,photo,userid) values (?,?,?,?,?,?,?,?,?,?,?)",auction.getUsername(),auction.getBid(),auction.getRangelowbid(),auction.getRangehighbid(),auction.getProductname(),auction.getCategory(),auction.getDescription(),auction.getAgeproduct(),auction.getTime(),auction.getPhoto(),auction.getUserid());
	}
	
	//get all product
	public List<AuctionBean> getAuction(){
		List<AuctionBean> auctionProduct= st.query("select userid,auctionid,username,bid,highbid,rangelowbid,rangehighbid,productname,category,description,ageproduct,time,photo,userid from auctions",new BeanPropertyRowMapper<AuctionBean>(AuctionBean.class));
		
		List<AuctionBean> allProducts=new ArrayList<AuctionBean>();
		for(AuctionBean pro:auctionProduct) {
			if(pro.getPhoto()!=null) {
//				List<FileDB> file=st.query("select * from files where id=?",new BeanPropertyRowMapper<FileDB>(FileDB.class),new Object[] {pro.getPhoto()});
				FileDBA file=fileDBARepository.findById(pro.getPhoto()).get();


				pro.setPhoto(Base64.getEncoder().encodeToString(file.getData()));
				allProducts.add(pro);
			}
		}	
		return allProducts;
	}
	
	//get particular product
	public AuctionBean getParAucProduct(long auctionid) {
		AuctionBean bean= st.query("select userid,auctionid,username,bid,highbid,rangelowbid,rangehighbid,productname,category,description,ageproduct,time,photo,biduser from auctions where auctionid=?",new BeanPropertyRowMapper<AuctionBean>(AuctionBean.class),new Object[] {auctionid}).get(0);
		
			if(bean.getPhoto()!=null) {
//				List<FileDB> file=st.query("select * from files where id=?",new BeanPropertyRowMapper<FileDB>(FileDB.class),new Object[] {pro.getPhoto()});
				FileDBA file=fileDBARepository.findById(bean.getPhoto()).get();


				bean.setPhoto(Base64.getEncoder().encodeToString(file.getData()));
				
			}
		return bean ;
	}
	
	//change the user
	public void updateBidUser(AuctionBean auction) {
//		AuctionBean ac=st.query("select * from auctions where auctionid=?",new BeanPropertyRowMapper<AuctionBean>(AuctionBean.class),new Object[] {auction.getAuctionid()}).get(0);
		st.update("update  auctions set biduser=? where auctionid=? ",auction.getBiduser(),auction.getAuctionid());
	}
	
	//get Product using userid
	public List<UserBeanAuth> getUserAuction(long userid){
		List<AuctionBean> au = st.query("select * from auctions where userid=?",new BeanPropertyRowMapper<AuctionBean>(AuctionBean.class),new Object[] {userid} );
		List<UserBeanAuth> users=new ArrayList<UserBeanAuth>();
		for(AuctionBean a:au) {
			if(a.getBiduser()!=null) {
				JSONArray j=new JSONArray(a.getBiduser());
				for(int i=0;i<j.length();i++) {
					JSONObject obj = new JSONObject(j.get(i).toString());
//					System.out.println(obj.getString("bid")+" "+obj.get("userid"));
					UserBeanAuth user=st.query("select * from usersa where userid=?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {obj.get("userid")}).get(0);
					user.setCartdata(obj.getString("bid"));
					user.setProductid(Integer.toString(a.getAuctionid()));
					users.add(user);
				}
				
				
			}
		}
		return users;
	}
	
	//setHighBit
	public void setHighBit(int userid,int auctionid) {
		st.update("update auctions set highbid=? where auctionid=?",userid,auctionid);
	}
}
