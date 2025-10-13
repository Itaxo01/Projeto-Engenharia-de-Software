package com.example.repository;

import com.example.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface JPA Repository para operações de banco de dados da entidade Professor.
 */
@Repository
public interface ProfessorJpaRepository extends JpaRepository<Professor, String> {
    
    /**
     * Busca professores por nome (case insensitive).
     */
    ArrayList<Professor> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca professor por nome exato.
     */
    Optional<Professor> findByNome(String nome);
    
    /**
     * Busca professores por disciplina.
     */
    @Query("SELECT p FROM Professor p JOIN p.disciplinas d WHERE d.codigo = :codigoDisciplina")
    ArrayList<Professor> findByDisciplinaCodigo(@Param("codigoDisciplina") String codigoDisciplina);
    
    /**
     * Busca professor com suas disciplinas carregadas.
     */
    @Query("SELECT DISTINCT p FROM Professor p LEFT JOIN FETCH p.disciplinas WHERE p.ID_LATTES = :idLattes")
    Optional<Professor> findByIdWithDisciplinas(@Param("idLattes") String idLattes);
    
    /**
     * Busca professor com suas avaliações carregadas.
     */
    @Query("SELECT DISTINCT p FROM Professor p LEFT JOIN FETCH p.avaliacoes WHERE p.ID_LATTES = :idLattes")
    Optional<Professor> findByIdWithAvaliacoes(@Param("idLattes") String idLattes);
    
    /**
     * Conta professores por disciplina.
     */
    @Query("SELECT COUNT(p) FROM Professor p JOIN p.disciplinas d WHERE d.codigo = :codigoDisciplina")
    long countByDisciplinaCodigo(@Param("codigoDisciplina") String codigoDisciplina);
}
