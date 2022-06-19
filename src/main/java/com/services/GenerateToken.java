package com.services;

import org.springframework.stereotype.Service;

@Service
public class GenerateToken {
	
	public String generateToken(int len) {
		String str="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		String str1="";
		
		for(int i=0;i<len;i++) {
			int ind=(int)(61*Math.random());
			System.out.println(ind);
			str1=str1+str.charAt(ind);
		}
		System.out.println(str1);
		return str1;
	}
	
}
