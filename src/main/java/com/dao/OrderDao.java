package com.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.FileDB;
import com.bean.OrderBean;
import com.bean.ProductBean;
import com.bean.UserBean;
import com.bean.UserBeanAuth;

@Repository
public class OrderDao {
	@Autowired
	JdbcTemplate st;
	
	@Autowired
	private FileDBRepository fileDBRepository;
	//write a customer which run every record query to update all authtoken in user table and change the status of 
	//order (processing,shipped,confirmed) based on date -> (1 day for shipped and 2 day for delivered)
	//SELECT date_part('month', timestamp '2022-07-22');current month,year,day
	
	
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
		public boolean userAuthentication(String authtoken) {
			List<UserBeanAuth> user = st.query("select * from usersa where  authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {authtoken});
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
					st.update("insert into orders (userid,orderdata,billname,ordernote,payinfo,billaddress,billamount,billtax,status,orderdate) values (?,?,?,?,?,?,?,?,'Processing',CURRENT_DATE)",order.getUserid(),order.getOrderdata(),order.getBillname(),order.getOrdernote(),order.getPayinfo(),order.getBilladdress(),billAmount,0);
				}else {
					st.update("insert into orders (userid,orderdata,billname,ordernote,payinfo,billaddress,billamount,billtax,status,orderdate) values (?,?,?,?,?,?,?,?,'Processing',CURRENT_DATE)",order.getUserid(),order.getOrderdata(),order.getBillname(),order.getOrdernote(),order.getPayinfo(),order.getBilladdress(),billAmount,50);
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
		public OrderBean getOrdersAuth(String authtoken,int orderid) {
			List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ? ", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {authtoken});
			if(user.size() == 0 || user == null) {
				return null;
			}else {
				List<OrderBean> bean= st.query("select * from orders where userid=? and orderid=?", 
						new BeanPropertyRowMapper<OrderBean>(OrderBean.class), new Object[] { user.get(0).getUserid(),orderid });
				String orderData[] = bean.get(0).getOrderdata().split(",");
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for(String order : orderData) {
					List<ProductBean> product = st.query("select productname,price from products where productid=?", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {Integer.parseInt(order)});
					sb.append("{\"productname\":\""+product.get(0).getProductname()+"\",\"price\":\""+product.get(0).getPrice()+"\"}").append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("]");
				bean.get(0).setOrderdata(sb.toString());
				return bean.get(0);
			}
		}
		
		//return all orders for particular user
		public List<OrderBean> getAllOrders(String authtoken)  {
			List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {authtoken});
			if(user.size() == 0 || user == null) {
				return null;
			}else {
				
				List<OrderBean> bean= st.query("select * from orders where userid=?", 
						new BeanPropertyRowMapper<OrderBean>(OrderBean.class), new Object[] { user.get(0).getUserid() });
				
				//convert string to date

				for(OrderBean x:bean) {
					if(x.getOrderdate()!=null) {
						final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						 try {
							Date date=sdf.parse(x.getOrderdate());
							java.util.Date date1=new java.util.Date();
							long dateBeforeInMs = date.getTime();
							long dateAfterInMs = date1.getTime();
							long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);
							long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
//							x.setTimeDay((int)daysDiff);
							if((int)daysDiff==1) {
								x.setStatus("Shipped");
							}else if((int)daysDiff>=2) {
								x.setStatus("Delivered");
							}else {
								x.setStatus("Processing");
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					//update query for order
					st.update("update orders set status=? where orderid=?",x.getStatus(),x.getOrderid());
					String orderData[] = x.getOrderdata().split(",");
					StringBuilder sb = new StringBuilder();
					for(String order : orderData) {
						List<ProductBean> product = st.query("select productname,price,photo from products where productid=?", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {Integer.parseInt(order)});
						System.out.println("in");
						if(product.get(0).getPhoto()!=null) {
							System.out.println("this in");
							FileDB file=fileDBRepository.findById(product.get(0).getPhoto()).get();
							String str=Base64.getEncoder().encodeToString(file.getData());
						    sb.append("{'productname':'"+product.get(0).getProductname()+"','photo':'"+str+"','price':'"+product.get(0).getPrice()+"'}").append(",");
						}
						else {
							sb.append("{'productname':'"+product.get(0).getProductname()+"','photo':'"+""+"','price':'"+product.get(0).getPrice()+"'}").append(",");
						}
					}
//					System.out.println("Stringbuilder-->"+sb.length());
					sb.deleteCharAt(sb.length() - 1);
					x.setOrderdata(sb.toString());
					
				}
				return bean;
			}
		}
		
		//delete order
		public boolean deleteOrder(String authtoken,int orderid) {
			List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ? ", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {authtoken});
			if(user.size() == 0 || user == null) {
				return false;
			}else {
				st.update("delete from orders where orderid=?",orderid);
				return true;
			}
		}
		
		
}
