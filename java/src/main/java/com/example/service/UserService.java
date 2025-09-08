package com.example.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    
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
    
    public CreationResult createUser(String email, String password) {
        if (emailExists(email)) {
            return new CreationResult(false, "Email already registered.");
        }
        
        users.put(email, password);
        saveUsersToFile();
        return new CreationResult(true, "User created successfully.");
    }
    
    public boolean validateUser(String email, String password) {
        return password.equals(users.get(email));
    }
}