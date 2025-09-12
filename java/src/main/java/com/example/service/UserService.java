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

	public record CreationResult(boolean success, String message) {}
    
    public CreationResult createUser(String email, String password) {
        if (userRepository.emailExists(email)) {
            return new CreationResult(false, "Email already registered.");
        }
		  String hashPassword = HashingService.hashPassword(password);
		  userRepository.createUser(email, hashPassword);
        return new CreationResult(true, "Conta criada com sucesso");
    }
    
    public boolean validateUser(String email, String password) {
		  if (!userRepository.emailExists(email)) {
				return false;
		  }
        String storedHash = userRepository.getPassword(email);
        return HashingService.verifyPassword(password, storedHash);
    }
}