package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bean.OrderBean;
import com.bean.ResponceUserBeanAuth;
import com.dao.OrderDao;

@RestController
@CrossOrigin
public class OrderController {

	@Autowired
	OrderDao orderDao;
	
	@PostMapping("/order")
	public boolean addOrder(@RequestBody OrderBean order) {
		return orderDao.addOrder(order);
	}
	
	@RequestMapping(value="/orders/{userid}",method=RequestMethod.GET)
	public OrderBean getOrders(@PathVariable int userid) {
		return orderDao.getOrders(userid);
	}
	
	@GetMapping("/order")
	public ResponceUserBeanAuth<?> getOrdersAuth(@RequestHeader("userId") int userId, @RequestHeader("authToken") String authToken){
		ResponceUserBeanAuth<OrderBean> orders = new ResponceUserBeanAuth<OrderBean>();
		OrderBean order = orderDao.getOrdersAuth(userId, authToken);
		if(order == null) {
			orders.setData(null);
			orders.setMsg("Unauthorized");
			orders.setStatus(401);
		}else {
			orders.setData(order);
			orders.setMsg("Get All Products Successfully");
			orders.setStatus(200);
		}
		return orders;
	}

}
