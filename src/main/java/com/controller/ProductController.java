package com.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	
	@RequestMapping(value="products/{id}/{userid}",method = RequestMethod.GET)
	public ProductBean getParticularProduct(@PathVariable long id,@PathVariable long userid) {
//		System.out.println("id "+ id);
//		System.out.println("id1 "+ id1);
		return productDao.getParticularProduct(id,userid);
	}
	
	@RequestMapping(value="products/{userid}",method=RequestMethod.GET)
	public List<ProductBean> getAllViewCartProducts(@PathVariable long userid){
		return productDao.getAllViewCartProducts(userid);
	}
	
	
	
	
	@PostMapping("/product")
	public ProductBean addProduct(@RequestBody ProductBean product) {
//		System.out.println(product.getPhoto());
//		JSONArray arr=new JSONArray(product.getPhoto());
//		System.out.println(arr);
//		for(int i=0;i<arr.length();i++) {
////			System.out.println(arr.get(i).toString());
//			JSONObject object=new JSONObject(arr.get(i).toString());
//			System.out.println(object.getString("name"));
//			System.out.println(object.getString("type"));
//			System.out.println(object.getInt("size"));
//			File stadir=new File("/media/vraj/New Volume/ComputerLanguages/Springlinux/demo/src/main/resources/static");
//			File f=new File(stadir,object.getString("name"));
//			try {
//				byte[] b=object.getString("name").getBytes();
//				FileOutputStream fo=new FileOutputStream(f);
//				fo.write(b);
//				fo.close();
//			}catch (Exception e) {
//				System.out.println("in");
//				e.printStackTrace();
//			}
//		}
//		System.out.println(arr.getClass().getSimpleName());
		
//		productDao.addProduct(product);
//		System.out.println(product.getPhoto());
		return product;
	}
}
