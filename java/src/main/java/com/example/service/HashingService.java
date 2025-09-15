package com.example.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Serviço utilitário para hashing e verificação de senhas usando BCrypt.
 */
public class HashingService {

	/**
	 * Gera um hash BCrypt para a senha informada.
	 * @param password senha em texto claro
	 * @return hash no formato $2a/$2b contendo salt
	 */
	public static String hashPassword(String password) {
		return BCrypt.withDefaults().hashToString(10, password.toCharArray());
	}

	/**
	 * Verifica se a senha em texto claro corresponde ao hash armazenado.
	 * @param password senha em texto claro
	 * @param hashed   hash previamente gerado
	 * @return true se a senha confere
	 */
	public static boolean verifyPassword(String password, String hashed) {
		BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashed);
		return result.verified;
	}
}
