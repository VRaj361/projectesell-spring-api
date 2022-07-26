package com.controller;

import java.util.List;

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
	
	//before add order
	@PostMapping("/order")
	public boolean addOrder(@RequestBody OrderBean order) {
		return orderDao.addOrder(order);
	}
	
	//after add order
	@PostMapping("/orderauth")
	public ResponceUserBeanAuth<?> addOrderAuth(@RequestBody OrderBean order,@RequestHeader("authtoken") String authtoken) {
		ResponceUserBeanAuth<OrderBean> orders = new ResponceUserBeanAuth<OrderBean>();
		boolean check = orderDao.userAuthentication( authtoken);
		
		if(check) {
			boolean check_product = orderDao.addOrderAuth(order);
			if(check_product) {
				orders.setData(order);
				orders.setStatus(200);
				orders.setMsg("Order Add Successfully");
			}else {
				orders.setData(null);
				orders.setStatus(500);
				orders.setMsg("Internal Server Error");
			}
		}else {
			orders.setData(null);
			orders.setStatus(401);
			orders.setMsg("Unauthorized");
		}
		return orders;
	}
	
	
	//before to get orders
	@RequestMapping(value="/orders/{userid}",method=RequestMethod.GET)
	public OrderBean getOrders(@PathVariable int userid) {
		return orderDao.getOrders(userid);
	}
	
	//after get order using authentication
	@GetMapping("/order")
	public ResponceUserBeanAuth<?> getOrdersAuth( @RequestHeader("authtoken") String authtoken){
		ResponceUserBeanAuth<OrderBean> orders = new ResponceUserBeanAuth<OrderBean>();
		OrderBean order = orderDao.getOrdersAuth( authtoken);
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
	
	//return all orders with detail for particular user
	@GetMapping("allorders")
	public ResponceUserBeanAuth<?> getAllOrders(@RequestHeader("authtoken") String authtoken){
		ResponceUserBeanAuth<List<OrderBean>> orders=new ResponceUserBeanAuth<List<OrderBean>>();
		List<OrderBean> order = orderDao.getAllOrders( authtoken);
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
