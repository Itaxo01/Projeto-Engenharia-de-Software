package com.example.repository;

import com.example.model.MapaCurricular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapaCurricularRepository extends JpaRepository<MapaCurricular, Long> {
    
    // Buscar todas as disciplinas do mapa de um usuário
    List<MapaCurricular> findByUserEmail(String userEmail);
    
    // Verificar se uma disciplina já está no mapa do usuário
    Optional<MapaCurricular> findByUserEmailAndDisciplinaId(String userEmail, String disciplinaId);
    
    // Deletar uma disciplina do mapa
    void deleteByUserEmailAndDisciplinaId(String userEmail, String disciplinaId);
}