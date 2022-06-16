package com.bean;

import org.springframework.web.multipart.MultipartFile;

public class FileBean {
	MultipartFile file;//
	String name; // form field

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
