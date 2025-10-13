package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.model.User;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

/**
 * Repositório em memória com persistência para usuários. Utiliza JPA para operações no banco de dados, as operações aqui são apenas para manter a interface consistente com o restante do código.
 * O id principal é o email, matrícula é única.
 */
@Repository
public class UserRepository {
    @Autowired
	 private UserJpaRepository jpaRepository;
    
    public boolean emailExists(String email) { return jpaRepository.existsById(email); }

	public boolean idExists(String id){ return jpaRepository.existsByMatricula(id); }

	/** Retorna a senha (já em hash) de um usuário. */
    public String getPassword(String email){ return jpaRepository.findById(email).map(User::getPassword).orElse(null); }

	/** Retorna o usuário associado ao email, ou null se não existir. */
	public User getUser(String email){ return jpaRepository.findById(email).orElse(null); }

   public List<User> getUsers() { return jpaRepository.findAll(); }

	public Boolean getAdmin(String email){ return jpaRepository.findById(email).map(User::getAdmin).orElse(false); }

	public void setAdmin(String email, boolean isAdmin){
		jpaRepository.findById(email).ifPresent(user -> {
			user.setAdmin(isAdmin);
			jpaRepository.save(user);
		});
	}

	public void createUser(String email, String password, String nome, String matricula, String curso){
		if (emailExists(email) || idExists(matricula)) {
			throw new IllegalArgumentException("Email ou matrícula já registrados.");
		}

		User u = new User(email, password, nome, matricula, curso);
		jpaRepository.save(u);
	}

	/** Deleta um usuário e salva em users.json. */
    public void deleteUser(String email){
		jpaRepository.deleteById(email);
    }

    public void changePassword(String email, String passwordHash) {
		jpaRepository.findById(email).ifPresent(user -> {
			user.setPassword(passwordHash);
			jpaRepository.save(user);
		});
    }

	 @PostConstruct
    public void migrateFromJsonIfNeeded() {
        if (jpaRepository.count() == 0) {
            loadUsersFromFile(); // Sua lógica existente de carregar JSON
        }
    }
    
    private void loadUsersFromFile() {
        // Sua lógica existente, mas salve no JPA:
        try {
            Path path = Paths.get("src/main/resources/users.json");
            if (Files.exists(path)) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, User> users = mapper.readValue(
                    Files.readString(path), 
                    new TypeReference<Map<String, User>>() {}
                );
                jpaRepository.saveAll(users.values());
                System.out.println("Migrados " + users.size() + " usuários para o banco");
            }
        } catch (Exception e) {
            System.err.println("Erro na migração: " + e.getMessage());
        }
    }
}