package com.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;

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

import com.bean.OrderBean;
import com.bean.ProductBean;
import com.bean.ResponceUserBean;
import com.bean.ResponceUserBeanAuth;
import com.bean.UserBeanAuth;
import com.dao.ProductDao;


@RestController
@CrossOrigin
public class ProductController {
	
	@Autowired
	ProductDao productDao;
	
	//list of all products
	@GetMapping("/products")
	public List<ProductBean> getAllProducts(){
		return productDao.getAllProducts();
	}
	 
	//particular Product
	@RequestMapping(value="product/{id}",method = RequestMethod.GET)
	public ProductBean getParticularProduct(@PathVariable long id) {
		return productDao.getParticularProduct(id);
	}
	
	//search products with using search box value
	@PostMapping("/products")
	public List<ProductBean> getParticularProducts(@RequestBody ProductBean bean){
		return productDao.getSearchProducts(bean.getProductname());
	}
	
	
	
	
	@RequestMapping(value="products/{id}/{userid}",method = RequestMethod.GET)
	public ProductBean getParticularProduct1(@PathVariable long id,@PathVariable long userid) {
		return productDao.getParticularProduct1(id,userid);
	}
	
	
	//before viewcart data
	@RequestMapping(value="products/{userid}",method=RequestMethod.GET)
	public List<ProductBean> getAllViewCartProducts(@PathVariable long userid){
		return productDao.getAllViewCartProducts(userid);
	}
	
	//after viewcart data
	@PostMapping("/productviewcart")
	public ResponceUserBeanAuth<List<ProductBean>> getAllViewCartProductsAuth(@RequestBody UserBeanAuth bean){
		ResponceUserBeanAuth<List<ProductBean>> res = new ResponceUserBeanAuth<>();
		List<ProductBean> products = productDao.getAllViewCartProductsAuth(bean);
		if(products != null) {
			res.setData(products);
			res.setStatus(200);
			res.setMsg("View Cart Products");
		}else {
			res.setData(null);
			res.setStatus(401);
			res.setMsg("Unauthorized");
		}
		return res;
	}
	
	//before delete particular product
	@RequestMapping(value="/productdelete/{productid}/{userid}",method=RequestMethod.GET)
	public void deleteParticularProduct(@PathVariable int productid,@PathVariable int userid) {
//		System.out.println(productid);
		productDao.deleteParticularProduct(productid,userid);
	}//delete particular product
	
	//after delete particular product
	@PostMapping("/productdelete")
	public ResponceUserBeanAuth<?> deleteParticularProduct(@RequestBody UserBeanAuth bean){
		ResponceUserBeanAuth<Boolean> res = new ResponceUserBeanAuth<>();  
		boolean check = productDao.deleteParticularProductsAuth(bean);
		if(check) {
			res.setData(true);
			res.setMsg("All Products Delete Successfully");
			res.setStatus(200);
		}else {
			res.setData(false);
			res.setMsg("Unauthorized");
			res.setStatus(401);
		}
		return res;
	}
	
	//before delete all product 
	@RequestMapping(value="/productdeleteall/{userid}",method=RequestMethod.GET)
	public boolean deleteAllProduct(@PathVariable int userid) {
		return productDao.deleteAllProduct(userid);
	}
	
	//after delete all products
	@PostMapping(value="/deleteAllProducts")
	public ResponceUserBeanAuth<?> deleteAllProductsAuth(@RequestBody UserBeanAuth bean) {
		ResponceUserBeanAuth<Boolean> res = new ResponceUserBeanAuth<>();  
		boolean check = productDao.deleteAllProductsAuth(bean);
		if(check) {
			res.setData(true);
			res.setMsg("All Products Delete Successfully");
			res.setStatus(200);
		}else {
			res.setData(false);
			res.setMsg("Unauthorized");
			res.setStatus(401);
		}
		return res;
		
	}
	

	
	
	
	
	
	@PostMapping("/order")
	public boolean addOrder(@RequestBody OrderBean order) {
		return productDao.addOrder(order);
	}
	
	@RequestMapping(value="/orders/{userid}",method=RequestMethod.GET)
	public OrderBean getOrders(@PathVariable int userid) {
		return productDao.getOrders(userid);
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
