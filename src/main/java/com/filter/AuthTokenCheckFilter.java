package com.filter;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class AuthTokenCheckFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req=((HttpServletRequest)(request));
		StringBuffer url=req.getRequestURL();
		System.out.println("url = "+url);
		
		String authToken=req.getHeader("authToken");
		String userStrId=req.getHeader("userid");
		System.out.println("token is => "+authToken);
//		if(url.toString().contains("")) {
//			chain.doFilter(request, response);
//		}
//		if(authToken==null||userStrId==null) {
//			HttpServletResponse res=((HttpServletResponse)(response));
//			res.setContentType("application/json");
//			res.setStatus(401);
//			res.getWriter().write("{'msg':'Please Login and access service'}");
//			
//		}else {
//			chain.doFilter(request, response);
//		}
		chain.doFilter(request, response);
	}

}
