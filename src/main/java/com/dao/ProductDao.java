package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.ProductBean;

@Repository
public class ProductDao {
	@Autowired
	JdbcTemplate st;
	
	public List<ProductBean> getAllProducts(){
		return st.query("select * from products",new BeanPropertyRowMapper<ProductBean>(ProductBean.class));
	}//get request
	
	public void addProduct(ProductBean product){
		st.update("insert into products (productname,price,description,location,title,userid) values (?,?,?,?,?,?)",product.getProductname(),product.getPrice(),product.getDescription(),product.getLocation(),product.getTitle(),product.getUserid());
	}//get request
	
	
}	
