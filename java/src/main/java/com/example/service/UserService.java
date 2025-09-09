package com.example.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServlet;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class UserService {
    
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final String USERS_FILE = "src/main/resources/users.properties";
    private final HashMap sessions = new HashMap<String,String>();

	public record CreationResult(boolean success, String message) {}



    public boolean createSession(String sessionId, String email) {
        if (sessionId.isEmpty() || email.isEmpty()) return false;
        sessions.put(sessionId, email);
        return true;
    }

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
		  String hashPassword = HashingService.hashPassword(password);
        users.put(email, hashPassword);
        saveUsersToFile();
        return new CreationResult(true, "User created successfully.");
    }
    
    public boolean validateUser(String email, String password) {
		  if (!emailExists(email)) {
				return false;
		  }
        String storedHash = users.get(email);
        return HashingService.verifyPassword(password, storedHash);
    }

	 public static boolean verifySession(HttpServletRequest request) {
		HttpSession session =  request.getSession(false);
		if (session == null) return false;
		try {
			String email = session.getAttribute("email").toString();
			return email != null && !email.isEmpty();
		} catch(Exception e) {
			return false;
		}
	}
}