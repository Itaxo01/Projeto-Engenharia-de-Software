package com.example.repository;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.model.User;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> matriculas = new ConcurrentHashMap<>(); // Associa uma matrícula a um email
    private final String USERS_JSON = "src/main/resources/users.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void loadUsersFromFile() {
        try {
            Path path = Paths.get(USERS_JSON);
            if (Files.exists(path)) {
                Map<String, User> node = mapper.readValue(Files.readString(path), new TypeReference<Map<String,User>>() {});
                users.putAll(node);
					 users.forEach((mail, user) -> 
					 	matriculas.put(user.getMatricula(), mail)
					 );
					 System.out.println("Loaded users: " + users.keySet());
				} else {
					 System.out.println("users.json file not found, starting with an empty user list.");
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    private void saveUsersToFile() {
        try {
            Path path = Paths.get(USERS_JSON);
            // Ensure parent directory exists
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            String tmp = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(users);
            Path tmpPath = path.resolveSibling(path.getFileName().toString() + ".tmp");
            Files.writeString(tmpPath, tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Failed to save users.json: " + e.getMessage());
        }
    }
    
    public boolean emailExists(String email) {
        return email != null && users.containsKey(email);
    }

	 public boolean idExists(String id){
		return matriculas.containsKey(id);
	 }

    public String getPassword(String email){
        User u = users.get(email);
        return u != null ? u.getPassword() : null;
    }

	 public User getUser(String email){
		if(!emailExists(email)) return null;
		return users.get(email);
	 }

    public void createUser(String email, String password, String nome, String matricula, String curso) {
        User user = new User(email, password, nome, matricula, curso);
        users.put(user.getEmail(), user);
        saveUsersToFile();
    }

    public void deleteUser(String email){
        users.remove(email);
        saveUsersToFile();
    }


}