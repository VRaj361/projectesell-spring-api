package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bean.ProductBean;
import com.dao.ProductDao;

@RestController
@CrossOrigin
public class ProductController {
	
	@Autowired
	ProductDao productDao;
	
	@GetMapping("/products")
	public List<ProductBean> getAllProducts(){
		return productDao.getAllProducts();
	}
	
	@PostMapping("/product")
	public ProductBean addProduct(@RequestBody ProductBean product) {
		productDao.addProduct(product);
		return product;
	}
}
