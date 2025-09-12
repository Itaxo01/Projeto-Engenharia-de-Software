package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.example.repository.UserRepository;


@Service
public class UserService {
    @Autowired
	 private UserRepository userRepository;

	public record QueryResult(boolean success, String message) {}
    
    public QueryResult createUser(String email, String password, String nome, String matricula, String curso) {
        if (userRepository.emailExists(email)) {
            return new QueryResult(false, "Email já registrado.");
        }
		  String hashPassword = HashingService.hashPassword(password);
		  userRepository.createUser(email, hashPassword, nome, matricula, curso);
        return new QueryResult(true, "Conta criada com sucesso");
    }

	 public QueryResult deleteUser(String email){
		if(!userRepository.emailExists(email)){
			return new QueryResult(false, "Essa conta não existe");
		}
		userRepository.deleteUser(email);
		return new QueryResult(true, "Conta deletada com sucesso!");
	 }
    
    public boolean validateUser(String email, String password) {
		  if (!userRepository.emailExists(email)) {
				System.out.println("Email not found: " + email);
				return false;
		  }
        String storedHash = userRepository.getPassword(email);
        return HashingService.verifyPassword(password, storedHash);
    }

	 public static String normalizeEmail(String email){
		if(email == null) return null;
		email = email.trim().toLowerCase();
		if(email.endsWith("@gmail.com")){
		  String[] parts = email.split("@");
        String localPart = parts[0];
        
        localPart = localPart.replace(".", "");
        
        if (localPart.contains("+")) {
            localPart = localPart.substring(0, localPart.indexOf("+"));
        }
        
        email = localPart + "@gmail.com";
		}
		return email;
	 }
}