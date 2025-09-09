package com.example.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashingService {

	public static String hashPassword(String password) {
		return BCrypt.withDefaults().hashToString(10, password.toCharArray());
	}

	public static boolean verifyPassword(String password, String hashed) {
		BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashed);
		return result.verified;
	}
}
