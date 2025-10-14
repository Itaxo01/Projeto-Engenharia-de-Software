package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Professor;

/**
 * Repository consolidado para Professor que herda diretamente de JpaRepository.
 */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, String> {
    
    /**
     * Busca professores por nome (case insensitive).
     */
    ArrayList<Professor> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca professor por nome exato.
     */
    Optional<Professor> findByNome(String nome);
    
    
    /**
     * Busca professor por nome exato (alias para compatibilidade).
     */
    default Optional<Professor> findByNomeExato(String nome) {
        return findByNome(nome);
    }
    
    /**
     * Conta total de professores.
     */
    long count();
}
