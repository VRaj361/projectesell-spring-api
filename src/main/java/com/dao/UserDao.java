package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.bean.UserBean;
import com.bean.UserBeanAuth;
import com.google.api.services.drive.model.User;

@Repository
public class UserDao {
	
	@Autowired
	JdbcTemplate st;
	
	@Autowired
	BCryptPasswordEncoder bcypt;
	
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
			user.setPassword(bcypt.encode(user.getPassword()));
			st.update("update usersa set password=? where authtoken=?",user.getPassword(),user.getAuthtoken());
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
	public boolean checkUser(UserBeanAuth user) {
		List<UserBeanAuth> users = st.query("select * from usersa where authtoken = ?  ", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getAuthtoken()});
		if(users.size()==0 || users == null) {
			return false;
		}else {
			return true;
		}
	}
	
}
