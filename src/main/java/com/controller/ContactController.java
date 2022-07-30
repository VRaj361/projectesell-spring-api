package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bean.ContactBean;
import com.bean.ResponceUserBeanAuth;
import com.bean.UserBeanAuth;
import com.dao.ContactDao;
import com.services.EmailService;
import com.services.GenerateToken;

@RestController
@CrossOrigin
public class ContactController {
	
	@Autowired
	ContactDao contactDao;
	
	@Autowired
	EmailService service;
	
	//get contact and send mail
	@PostMapping("/contact")
	public ResponceUserBeanAuth<?> addContact(@RequestBody ContactBean bean) {
		ContactBean duplicate= contactDao.findContact(bean);
		ResponceUserBeanAuth<ContactBean> res=new ResponceUserBeanAuth<>();
		if(duplicate==null) {
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
}
