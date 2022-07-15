package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.UserBean;
import com.bean.UserBeanAuth;
import com.google.api.services.drive.model.User;

@Repository
public class UserDao {
	
	@Autowired
	JdbcTemplate st;
	
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
	
	
	
	//for users table without token
	
//	public UserBean findUser(String email) {
//		List<UserBean> users=st.query("select * from users where email = ?", new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[] {email});
//		if(users.size()==0) {
//			return null;//user does not exists
//		}else {
//			return users.get(0);//one user can exists
//		}
//	}
	
	public UserBeanAuth findUser(UserBeanAuth user) {
		List<UserBeanAuth> users=st.query("select * from usersa where email = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {user.getEmail()});
		if(users.size()==0) {
			st.update("insert into usersa (firstname,lastname,createdate,gender,email,password,phonenum,address) values (?,?,?,?,?,?,?,?)",user.getFirstname(),user.getLastname(),user.getCreatedate(),user.getGender(),user.getEmail(),user.getPassword(),user.getPhonenum(),user.getAddress());
			return null;//user does not exists
		}else {
			return users.get(0);//one user can exists
		}
	}
	
	public List<UserBeanAuth> getAllUserAuth(String token){
		return st.query("select * from usersa where authtoken=?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {token});
	}

	public void setToken(String email,String str) {
		// TODO Auto-generated method stub
		st.update("update usersa set authtoken = ? where email=?",str,email);
	}
}
