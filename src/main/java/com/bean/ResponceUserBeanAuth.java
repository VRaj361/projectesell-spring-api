
package com.bean;

public class ResponceUserBeanAuth<T> {
	int status;
	String msg;
	T data;
//	String auth;
//	public String getAuth() {
//		return auth;
//	}
//	public void setAuth(String auth) {
//		this.auth = auth;
//	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
