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

/**
 * Repositório em memória com persistência em arquivo JSON para usuários.
 */
@Repository
public class UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>(); // Associa um email a um usuário
    private final Map<String, String> matriculas = new ConcurrentHashMap<>(); // Associa uma matrícula a um email
    private final String USERS_JSON = "src/main/resources/users.json";
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Carrega usuários de users.json ao iniciar a aplicação.
     */
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
					 System.out.println("Usuários carregados: " + users.size());
				} else {
					System.err.println("Erro ao carregar usuários: " + "Arquivo users.json não encontrado, iniciando com uma lista de usuários vazia.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
        }
    }
    
    /**
     * Salva usuários em users.json após qualquer modificação. Isso obviamente é lento e pode gerar erros de concorrência, mas é ok enquanto não migramos para um banco de dados real.
     */
    private void saveUsersToFile() {
        try {
            Path path = Paths.get(USERS_JSON);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            String tmp = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(users);
            Path tmpPath = path.resolveSibling(path.getFileName().toString() + ".tmp");
            Files.writeString(tmpPath, tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Falha ao salvar users.json: " + e.getMessage());
        }
    }
    
	 /** Verifica se um email já está cadastrado. */
    public boolean emailExists(String email) {
        return email != null && users.containsKey(email);
    }

	 /** Verifica se uma matrícula já está cadastrada. */
	public boolean idExists(String id){
		return matriculas.containsKey(id);
	}

	/** Retorna a senha (já em hash) de um usuário. */
    public String getPassword(String email){
        User u = users.get(email);
        return u != null ? u.getPassword() : null;
    }

	/** Retorna o usuário associado ao email, ou null se não existir. */
	public User getUser(String email){
		if(!emailExists(email)) return null;
		return users.get(email);
	}

	 /** Cria um novo usuário e salva em users.json. Os possiveis conflitos são resolvidos aqui e também nas classes que chamam a função, para garantir a consistência dos dados. */
    public void createUser(String email, String password, String nome, String matricula, String curso) {
		if(emailExists(email) || idExists(matricula)){
			throw new IllegalArgumentException("Email ou matrícula já cadastrados.");
		}
		User user = new User(email, password, nome, matricula, curso);
		users.put(user.getEmail(), user);
		saveUsersToFile();
    }

	/** Deleta um usuário e salva em users.json. */
    public void deleteUser(String email){
		if(!emailExists(email)) return;
		users.remove(email);
		saveUsersToFile();
    }
}