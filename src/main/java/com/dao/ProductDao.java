package com.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.util.ArrayUtils;

import com.bean.ProductBean;
import com.bean.UserBean;

@Repository
public class ProductDao {
	@Autowired
	JdbcTemplate st;
	
	public List<ProductBean> getAllProducts(){
		return st.query("select * from products",new BeanPropertyRowMapper<ProductBean>(ProductBean.class));
	}//get request
	
	public ProductBean getParticularProduct(long id,long userid) {
		List<UserBean> cartdata=st.query("select cartdata from users where userid=?",new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[] {userid});
		String cartData=cartdata.get(0).getCartdata();
		if(cartData==null) {
			st.update("update users set cartdata=? where userid=?",id,userid);
		}else {
			cartData=cartData+","+id;
			st.update("update users set cartdata=? where userid=?",cartData,userid);
		}
		List<ProductBean> bean= st.query("select * from products where productid=? ", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {id});
		return bean.get(0);
	}//get particular product
	
	public ProductBean getParticularProduc(long id) {
		List<ProductBean> bean= st.query("select * from products where productid=? ", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {id});
		return bean.get(0);
	}
	
	
	
	public List<ProductBean> getAllViewCartProducts(long userid){
		List<UserBean> cartData=st.query("select cartdata from users where userid=?", new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[] {userid});
		String data=cartData.get(0).getCartdata();
//		System.out.println("this is teh data "+ data+" tha");
		if(data.equals("")) {
//			System.out.println("in");
			return null;
		}else {
			if(data!=null) {
				List<ProductBean> allProductsBeans=new ArrayList<ProductBean>();
				if(data.charAt(0)==',') {
					data=data.substring(1);
				}
				String[] arr=data.split(",");
				Set<String> set = new HashSet<>(Arrays.asList(arr));

				for(String x:set) {
					int x1=Integer.parseInt(x);
					List<ProductBean> bean= st.query("select * from products where productid=? ", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {x1});
					allProductsBeans.add(bean.get(0));
				}
				return allProductsBeans;
			}
			return null;
		}
		
	}
	
	
	public void deleteParticularProduct(int productid,int userid) {
		List<UserBean> cartdata=st.query("select cartdata from users where userid=?",new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[]{userid});
		String cartData=cartdata.get(0).getCartdata();
		String str="";
		if(cartData.equals("")) {
			str=str+productid;
		}else {
			
			if(cartData.charAt(0)==',') {
				cartData=cartData.substring(1);
			}
			
			
			
			String arr[]=cartData.split(",");
			System.out.println(Arrays.toString(arr));
			if(Integer.parseInt(arr[0])!=productid) {
				str=str+arr[0];
				System.out.println("this");
			}
			for(int i=1;i<arr.length;i++) {
				
				if(Integer.parseInt(arr[i])!=productid) {
					str=str+","+arr[i];
					System.out.println("this taht");
				}else if(Integer.parseInt(arr[i])!=productid){
					str=str+arr[i];
					System.out.println("that");
				}
					
			}
		}
		System.out.println(str);
		st.update("update users set cartdata=? where userid=?",str,userid);
		
	}
	
	
	
	
	
	public void addProduct(ProductBean product){
		st.update("insert into products (productname,price,description,location,title,userid,rating,productid) values (?,?,?,?,?,?,?,?)",product.getProductname(),product.getPrice(),product.getDescription(),product.getLocation(),product.getTitle(),product.getUserid(),product.getRating(),product.getProductid());
//		st.update("insert into products (productname,price,description,location,title,userid,rating,photo) values (?,?,?,?,?,?,?,?)",product.getProductname(),product.getPrice(),product.getDescription(),product.getLocation(),product.getTitle(),product.getUserid(),product.getRating(),product.getPhoto());
	}//get request
	
	
}	
