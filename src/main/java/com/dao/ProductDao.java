package com.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.util.ArrayUtils;

import com.bean.OrderBean;
import com.bean.ProductBean;
import com.bean.ResponceUserBeanAuth;
import com.bean.UserBean;
import com.bean.UserBeanAuth;

@Repository
public class ProductDao {
	@Autowired
	JdbcTemplate st;

	//return all product without verify
	public List<ProductBean> getAllProducts() {
		return st.query("select * from products", new BeanPropertyRowMapper<ProductBean>(ProductBean.class));
	}

	//return product without verify
	public ProductBean getParticularProduct(long id) {
		List<ProductBean> bean = st.query("select * from products where productid=? ",
				new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { id });
		return bean.get(0);
	}
	
	//search the product for search box
	public List<ProductBean> getSearchProducts(String name){
		return st.query("select * from products where productname like CONCAT( '%',?,'%') or title like CONCAT( '%',?,'%') or description like CONCAT( '%',?,'%')", new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { name,name,name });
	}
	
	
	
	
	//user cartdata
	public ProductBean getParticularProduct1(long id, long userid) {
		List<UserBean> cartdata = st.query("select cartdata from users where userid=?",
				new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { userid });
		String cartData = cartdata.get(0).getCartdata();
		if (cartData == null) {
			st.update("update users set cartdata=? where userid=?", id, userid);
		} else {
			cartData = cartData + "," + id;
			st.update("update users set cartdata=? where userid=?", cartData, userid);
		}
		List<ProductBean> bean = st.query("select * from products where productid=? ",
				new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { id });
		return bean.get(0);
	}// get particular product

	
	
	
	
	
	//before view cart products
	public List<ProductBean> getAllViewCartProducts(long userid) {
		List<UserBean> cartData = st.query("select cartdata from users where userid=?",
				new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { userid });
		String data = cartData.get(0).getCartdata();

		if (data.equals("")) {

			return null;
		} else {
			if (data != null) {
				List<ProductBean> allProductsBeans = new ArrayList<ProductBean>();
				if (data.charAt(0) == ',') {
					data = data.substring(1);
				}
				String[] arr = data.split(",");
				Set<String> set = new HashSet<>(Arrays.asList(arr));

				for (String x : set) {
					int x1 = Integer.parseInt(x);
					List<ProductBean> bean = st.query("select * from products where productid=? ",
							new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { x1 });
					allProductsBeans.add(bean.get(0));
				}
				return allProductsBeans;
			}
			return null;
		}

	}
	
	//after view cart products
	public List<ProductBean> getAllViewCartProductsAuth(UserBeanAuth bean){
		List<UserBeanAuth> user = st.query("select * from usersa where userid = ? and authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(),new Object[] {bean.getUserid(),bean.getAuthtoken()});
		if(user.size() == 0) {
			return null;
		}else {
			List<String> cartData = Arrays.asList(user.get(0).getCartdata().split(","));
			List<ProductBean> products = new ArrayList<>();
			for(String product:cartData) {
				int product_int = Integer.parseInt(product);
				List<ProductBean> product_get = st.query("select * from products where productid=? ",
						new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { product_int });
				products.add(product_get.get(0));
			}
			return products;
		}
	}
	
	
	//before delete particular product
	public void deleteParticularProduct(int productid, int userid) {
		List<UserBean> cartdata = st.query("select cartdata from users where userid=?",
				new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { userid });
		String cartData = cartdata.get(0).getCartdata();
		String str = "";
		if (cartData.equals("")) {
			str = str + productid;
		} else {

			if (cartData.charAt(0) == ',') {
				cartData = cartData.substring(1);
			}

			String arr[] = cartData.split(",");

			if (Integer.parseInt(arr[0]) != productid) {
				str = str + arr[0];

			}
			for (int i = 1; i < arr.length; i++) {

				if (Integer.parseInt(arr[i]) != productid) {
					str = str + "," + arr[i];

				} else if (Integer.parseInt(arr[i]) != productid) {
					str = str + arr[i];

				}

			}
		}

		st.update("update users set cartdata=? where userid=?", str, userid);

	}
	
	//after delete particular product
	public boolean deleteParticularProductsAuth(UserBeanAuth bean) {
		List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ?",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {bean.getAuthtoken()});
		if(user.size()==1) {
			List<UserBean> cartdata = st.query("select cartdata from usersa where userid=?",
					new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { bean.getUserid() });
			List<String> cartData = Arrays.asList(cartdata.get(0).getCartdata().split(","));
			int index = cartData.indexOf(bean.getProductid());
			if(index != -1) {
				cartData.remove(index);
				String str = String.join(",", cartData);
				System.out.println("Deleted value---->"+str);
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	
	// before delete all product
	public boolean deleteAllProduct(int userid) {
		st.update("update users set cartdata='' where userid=?", userid);
		return true;
	}
	
	//after delete all product
	public boolean deleteAllProductsAuth(UserBeanAuth bean) {
		List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ?",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {bean.getAuthtoken()});
		if(user.size()==1) {
			st.update("update usersa set cartdata = '' where userid=?", bean.getUserid());
			return true;
		}else {
			return false;
		}
	}
	
	
	
	
	
	
	// copy products detail to shift into order bean detail
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
	
	public OrderBean getOrders(int userid) {
		List<OrderBean> orders=st.query("select * from orders where userid=?", 
				new BeanPropertyRowMapper<OrderBean>(OrderBean.class), new Object[] { userid });
//		System.out.println("orders=> "+orders.get(0).getBilladdress());
		return orders.get(0);
	}

	public void addProduct(ProductBean product) {
		st.update(
				"insert into products (productname,price,description,location,title,userid,rating,productid) values (?,?,?,?,?,?,?,?)",
				product.getProductname(), product.getPrice(), product.getDescription(), product.getLocation(),
				product.getTitle(), product.getUserid(), product.getRating(), product.getProductid());
//		st.update("insert into products (productname,price,description,location,title,userid,rating,photo) values (?,?,?,?,?,?,?,?)",product.getProductname(),product.getPrice(),product.getDescription(),product.getLocation(),product.getTitle(),product.getUserid(),product.getRating(),product.getPhoto());
	}// get request

}
