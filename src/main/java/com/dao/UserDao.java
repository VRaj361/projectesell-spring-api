package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.UserBean;

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
}
