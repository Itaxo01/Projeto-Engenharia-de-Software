package com.example.repository;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.example.service.HashingService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class UserRepository {
    
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final String USERS_FILE = "src/main/resources/users.properties";

	public record CreationResult(boolean success, String message) {}


    @PostConstruct
    public void loadUsersFromFile() {
        try {
            Path path = Paths.get(USERS_FILE);
            if (Files.exists(path)) {
                Properties props = new Properties();
                props.load(Files.newInputStream(path));
                props.forEach((key, value) -> users.put(key.toString(), value.toString()));
            }
        } catch (IOException e) {
            // Handle error
        }
    }
    
    private void saveUsersToFile() {
        try {
            Path path = Paths.get(USERS_FILE);
            Files.createDirectories(path.getParent());
            
            Properties props = new Properties();
            users.forEach(props::setProperty);
            props.store(Files.newOutputStream(path), "User accounts");
        } catch (IOException e) {
            // Handle error
        }
    }
    
    public boolean emailExists(String email) {
        return users.containsKey(email);
    }


	 public String getPassword(String email){
			return users.get(email);
	 }

	 public void createUser(String email, String hashPassword) {
        users.put(email, hashPassword);
        saveUsersToFile();
    }

	 public void deleteUser(String email){
		users.remove(email);
		saveUsersToFile();
	 }


}