package com.example.repository;

import com.example.model.Disciplina;
import com.example.model.MapaCurricular;
import com.example.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapaCurricularRepository extends JpaRepository<MapaCurricular, Long> {
    
    // Buscar todas as disciplinas do mapa de um usuário
    List<MapaCurricular> findByUsuario(Usuario user);
    
    // Verificar se uma disciplina já está no mapa do usuário
    Optional<MapaCurricular> findByUsuarioAndDisciplina(Usuario usuario, Disciplina disciplina);
    
    // Deletar uma disciplina do mapa
    void deleteByUsuarioAndDisciplina(Usuario usuario, Disciplina disciplina);
}