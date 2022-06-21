package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.ProductBean;
import com.bean.UserBean;

@Repository
public class ProductDao {
	@Autowired
	JdbcTemplate st;
	
	public List<ProductBean> getAllProducts(){
		return st.query("select * from products",new BeanPropertyRowMapper<ProductBean>(ProductBean.class));
	}//get request
	
	public ProductBean getParticularProduct(long id,long id1) {
		List<UserBean> cartdata=st.query("select cartdata from users where userid=?",new BeanPropertyRowMapper<UserBean>(UserBean.class),new Object[] {id1});
		String cartData=cartdata.get(0).getCartdata();
		if(cartData==null) {
			st.update("update users set cartdata=? where userid=?",id,id1);
		}else {
			cartData=cartData+","+id;
			st.update("update users set cartdata=? where userid=?",cartData,id1);
		}
		List<ProductBean> bean= st.query("select * from products where productid=? ", new BeanPropertyRowMapper<ProductBean>(ProductBean.class),new Object[] {id});
		return bean.get(0);
	}//get particular product
	
	
	public void addProduct(ProductBean product){
		st.update("insert into products (productname,price,description,location,title,userid,rating,productid) values (?,?,?,?,?,?,?,?)",product.getProductname(),product.getPrice(),product.getDescription(),product.getLocation(),product.getTitle(),product.getUserid(),product.getRating(),product.getProductid());
//		st.update("insert into products (productname,price,description,location,title,userid,rating,photo) values (?,?,?,?,?,?,?,?)",product.getProductname(),product.getPrice(),product.getDescription(),product.getLocation(),product.getTitle(),product.getUserid(),product.getRating(),product.getPhoto());
	}//get request
	
	
}	
