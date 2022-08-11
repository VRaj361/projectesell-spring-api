package com.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.util.ArrayUtils;

import com.bean.FileBean;
import com.bean.FileDB;
import com.bean.OrderBean;
import com.bean.ProductBean;
import com.bean.ResponceUserBeanAuth;
import com.bean.UserBean;
import com.bean.UserBeanAuth;

@Repository
public class ProductDao {
	@Autowired
	JdbcTemplate st;

	@Autowired
	private FileDBRepository fileDBRepository;
	
	//return all product without verify
	public List<ProductBean> getAllProducts() {
		List<ProductBean> products= st.query("select * from products", new BeanPropertyRowMapper<ProductBean>(ProductBean.class));
		List<ProductBean> allProducts=new ArrayList<ProductBean>();
		for(ProductBean pro:products) {
			if(pro.getPhoto()!=null) {
//				List<FileDB> file=st.query("select * from files where id=?",new BeanPropertyRowMapper<FileDB>(FileDB.class),new Object[] {pro.getPhoto()});
				FileDB file=fileDBRepository.findById(pro.getPhoto()).get();


				pro.setPhoto(Base64.getEncoder().encodeToString(file.getData()));
				allProducts.add(pro);
			}
		}	
		return allProducts;
	}

	//return product without verify
	public ProductBean getParticularProduct(long id) {
		List<ProductBean> bean = st.query("select * from products where productid=? ",
				new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { id });
//		return bean.get(0);
		List<ProductBean> allProducts=new ArrayList<ProductBean>();
		for(ProductBean pro:bean) {
			if(pro.getPhoto()!=null) {
//				List<FileDB> file=st.query("select * from files where id=?",new BeanPropertyRowMapper<FileDB>(FileDB.class),new Object[] {pro.getPhoto()});
				FileDB file=fileDBRepository.findById(pro.getPhoto()).get();

				System.out.println("fgbajl-->"+Base64.getEncoder().encodeToString(file.getData()));
				pro.setPhoto(Base64.getEncoder().encodeToString(file.getData()));
				allProducts.add(pro);
			}
		}	
		return allProducts.get(0);
	}
	
	//search the product for search box
	public List<ProductBean> getSearchProducts(String name){
		return st.query("select * from products where productname like CONCAT( '%',?,'%') or title like CONCAT( '%',?,'%') or description like CONCAT( '%',?,'%')", new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { name,name,name });
	}
	
	//before
	//user cartdata add product or update product
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

	
	//after
	//addtoproduct
	public ProductBean addToCartProduct(UserBeanAuth bean) {
		List<UserBeanAuth> user = st.query("select * from usersa where authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {bean.getAuthtoken()});
		if(user.size() == 0) {
			return null;
		}else {
			if(user.get(0).getCartdata() == null || user.get(0).getCartdata().length() == 0) {
				st.update("update usersa set cartdata=? where userid=?", bean.getProductid(), user.get(0).getUserid());
			}else {
				List<String> cartData = new ArrayList<>(Arrays.asList(user.get(0).getCartdata().split(",")));
				int index = cartData.indexOf(bean.getProductid());
				if(index == -1) {
					cartData.add(bean.getProductid());
					String str = String.join(",", cartData);
					st.update("update usersa set cartdata=? where userid=?", str, user.get(0).getUserid());
				}
			}
			ProductBean pro= st.query("select * from products where productid = ?", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {Integer.parseInt(bean.getProductid())}).get(0);
			pro.setUserid(user.get(0).getUserid());
			return pro;
		}
	}
	
	
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
	public List<ProductBean> getAllViewCartProductsAuth(int userid,String authtoken){
		List<UserBeanAuth> user = st.query("select * from usersa where userid = ? and authtoken = ?", new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {userid,authtoken});
		if(user.size() == 0 || user.get(0).getCartdata().equals("")) {
			return null;
		}else {
			List<String> cartData = Arrays.asList(user.get(0).getCartdata().split(","));
			List<ProductBean> products = new ArrayList<>();
			for(String product:cartData) {
				int product_int = Integer.parseInt(product);
				List<ProductBean> product_get = st.query("select * from products where productid=? ",
						new BeanPropertyRowMapper<ProductBean>(ProductBean.class), new Object[] { product_int });
				System.out.println("photo--->"+ product_get.get(0).getPhoto());
				if(product_get.get(0).getPhoto()!=null) {
					FileDB file=fileDBRepository.findById(product_get.get(0).getPhoto()).get();
					product_get.get(0).setPhoto(Base64.getEncoder().encodeToString(file.getData()));
				}
				
				
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
		List<UserBeanAuth> user = st.query("select * from usersa where userid=? and authtoken = ?",new BeanPropertyRowMapper<UserBeanAuth>(UserBeanAuth.class),new Object[] {bean.getUserid(),bean.getAuthtoken()});
		if(user.size()==1) {
			List<UserBean> cartdata = st.query("select cartdata from usersa where userid=?",
					new BeanPropertyRowMapper<UserBean>(UserBean.class), new Object[] { bean.getUserid() });
			List<String> cartData = new ArrayList<>(Arrays.asList(cartdata.get(0).getCartdata().split(",")));
			int index = cartData.indexOf(bean.getProductid());
			if(index != -1) {
				cartData.remove(bean.getProductid());
				String str = String.join(",", cartData);
				st.update("update usersa set cartdata=? where userid=?", str, bean.getUserid());
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
			st.update("update usersa set cartdata = '' where userid=?", user.get(0).getUserid());
			return true;
		}else {
			return false;
		}
	}
	
	
	//add product
	public void addProduct(ProductBean product) {
		st.update(
				"insert into products (productname,price,description,title,userid,rating,photo) values (?,?,?,?,?,?,?)",
				product.getProductname(), product.getPrice(), product.getDescription(), 
				product.getTitle(), product.getUserid(), product.getRating(),product.getPhoto());

	}// get request

}
