package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.User;

/**
 * Repository consolidado para User que herda diretamente de JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Verifica se existe usuário com a matrícula.
     */
    boolean existsByMatricula(String matricula);
    
    /**
     * Métodos de conveniência implementados como default
     */
    default boolean emailExists(String email) { 
        return existsById(email); 
    }

    default boolean idExists(String id) { 
        return existsByMatricula(id); 
    }

    default String getPassword(String email) { 
        return findById(email).map(User::getPassword).orElse(null); 
    }

    default User getUser(String email) { 
        return findById(email).orElse(null); 
    }

    default List<User> getUsers() { 
        return findAll(); 
    }

    default Boolean getAdmin(String email) { 
        return findById(email).map(User::getAdmin).orElse(false); 
    }

    default void setAdmin(String email, boolean isAdmin) {
        findById(email).ifPresent(user -> {
            user.setAdmin(isAdmin);
            save(user);
        });
    }

    default void createUser(String email, String password, String nome, String matricula, String curso) {
        if (emailExists(email) || idExists(matricula)) {
            throw new IllegalArgumentException("Email ou matrícula já registrados.");
        }

        User u = new User(email, password, nome, matricula, curso);
        save(u);
    }

    default void deleteUser(String email) {
        deleteById(email);
    }

    default void changePassword(String email, String passwordHash) {
        findById(email).ifPresent(user -> {
            user.setPassword(passwordHash);
            save(user);
        });
    }
}