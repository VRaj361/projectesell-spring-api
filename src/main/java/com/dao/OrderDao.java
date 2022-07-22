package com.dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.OrderBean;
import com.bean.ProductBean;
import com.bean.UserBean;
import com.bean.UserBeanAuth;

@Repository
public class OrderDao {
	@Autowired
	JdbcTemplate st;
	
	// copy products detail to shift into order bean detail
	//before -> order create
		public boolean addOrder(OrderBean order) {
			List<UserBean> cartData = st.query("select cartdata from users where userid=?",
					new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { order.getUserid() });
			String data = cartData.get(0).getCartdata();

			if (data != null) {
				if (data.charAt(0) == ',') {
					data = data.substring(1);
				}
				String[] arr = data.split(",");
				Set<String> set = new HashSet<>(Arrays.asList(arr));

				String str = "";
				String[] arr1 = new String[set.size()];
				set.toArray(arr1);
				for (int i = 0; i < arr1.length; i++) {
					if (i == arr1.length - 1) {
						str += arr1[i];
					} else {
						str += arr1[i] + ',';
					}
				}
				order.setOrderdata(str);
				
				//calculate the bill amount
				int billAmount=0;
				for(String x:arr1) {
					int val=Integer.parseInt(x);
					List<ProductBean> prod =  st.query("select price from products where productid=?",
							new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { val });
					billAmount+=Integer.parseInt(prod.get(0).getPrice());
				}
				List<OrderBean> orders= st.query("select * from orders where userid=?",new BeanPropertyRowMapper<OrderBean>(OrderBean.class),new Object[] {order.getUserid()});
				if(orders.size()==0) {
					if(billAmount>500) {
						st.update("insert into orders (userid,orderdata,billname,ordernote,payinfo,billaddress,billamount,billtax) values (?,?,?,?,?,?,?,?)",order.getUserid(),order.getOrderdata(),order.getBillname(),order.getOrdernote(),order.getPayinfo(),order.getBilladdress(),billAmount,0);
					}else {
						st.update("insert into orders (userid,orderdata,billname,ordernote,payinfo,billaddress,billamount,billtax) values (?,?,?,?,?,?,?,?)",order.getUserid(),order.getOrderdata(),order.getBillname(),order.getOrdernote(),order.getPayinfo(),order.getBilladdress(),billAmount,50);
					}
					return true;
				}else {
					return false;
				}
			}
			return false;

		}
		
		
		//header to check user is exists or not
		public boolean userAuthentication(int userid,String authtoken) {
			List<UserBeanAuth> user = st.query("select * from usersa where userid = ? and authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {userid,authtoken});
			if(user.size() == 0 || user == null) {
				return false;
			}else {
				return true;
			}
		}
		
		//after product create
		public boolean addOrderAuth(OrderBean order) {

			List<UserBean> cartData = st.query("select cartdata from usersa where userid=?",
					new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { order.getUserid() });
			String data = cartData.get(0).getCartdata();
			if (data != null) {
				
				order.setOrderdata(data);
				String products[] = data.split(",");
				//calculate the bill amount
				int billAmount=0;
				for(String x:products) {
					int val=Integer.parseInt(x);
					List<ProductBean> prod =  st.query("select price from products where productid=?",
							new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { val });
					billAmount+=Integer.parseInt(prod.get(0).getPrice());
				}
				List<OrderBean> orders= st.query("select * from orders where userid=?",new BeanPropertyRowMapper<OrderBean>(OrderBean.class),new Object[] {order.getUserid()});
				//if required order unique then write logic here
				//condition orders.size()>=1 => check every order contain this order or not 
				if(billAmount>500) {
					st.update("insert into orders (userid,orderdata,billname,ordernote,payinfo,billaddress,billamount,billtax) values (?,?,?,?,?,?,?,?)",order.getUserid(),order.getOrderdata(),order.getBillname(),order.getOrdernote(),order.getPayinfo(),order.getBilladdress(),billAmount,0);
				}else {
					st.update("insert into orders (userid,orderdata,billname,ordernote,payinfo,billaddress,billamount,billtax) values (?,?,?,?,?,?,?,?)",order.getUserid(),order.getOrderdata(),order.getBillname(),order.getOrdernote(),order.getPayinfo(),order.getBilladdress(),billAmount,50);
				}
				st.update("update usersa set cartdata = '' where userid = ?",order.getUserid());
				return true;
			}
			return false;

		}
		
		//before get order
		public OrderBean getOrders(int userid) {
			List<OrderBean> orders=st.query("select * from orders where userid=?", 
					new BeanPropertyRowMapper<OrderBean>(OrderBean.class), new Object[] { userid });
//			System.out.println("orders=> "+orders.get(0).getBilladdress());
			return orders.get(0);
		}
		
		//after get order of particular user(authentication required)
		public OrderBean getOrdersAuth(int userid,String authtoken) {
			List<UserBeanAuth> user = st.query("select * from usersa where userid = ? and authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {userid,authtoken});
			if(user.size() == 0 || user == null) {
				return null;
			}else {
				return st.query("select * from orders where userid=?", 
						new BeanPropertyRowMapper<OrderBean>(OrderBean.class), new Object[] { userid }).get(0);
			}
		}
		
		
		
}
