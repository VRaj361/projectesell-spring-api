package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.ContactBean;
import com.bean.UserBeanAuth;

@Repository
public class ContactDao {
	@Autowired
	JdbcTemplate st;
	
	//check duplicate entry
	public ContactBean findContact(ContactBean user) {
		List<ContactBean> users=st.query("select * from contacts where email = ?", new BeanPropertyRowMapper<ContactBean>(ContactBean.class),new Object[] {user.getEmail()});
		if(users.size()==0) {
			st.update("insert into contacts (email,name,msg,subject) values (?,?,?,?)",user.getEmail(),user.getName(),user.getMsg(),user.getSubject());
			return null;//user does not exists
		}else {
			return users.get(0);//one user can exists
		}
	}
	
	
}
