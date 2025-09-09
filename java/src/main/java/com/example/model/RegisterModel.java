package com.example.model;

import org.springframework.web.multipart.MultipartFile;


public class RegisterModel {
	private String email;
	private String password;
	private MultipartFile file;

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.email;
	}

	public MultipartFile getFile() {
		return this.file;
	}
}