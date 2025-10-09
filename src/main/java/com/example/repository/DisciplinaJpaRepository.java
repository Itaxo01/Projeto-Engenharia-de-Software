package com.example.repository;

import com.example.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Interface JPA Repository para operações de banco de dados da entidade Disciplina.
 */
@Repository
public interface DisciplinaJpaRepository extends JpaRepository<Disciplina, Long> {
    
    /**
     * Busca disciplina pelo código único.
     */
    Optional<Disciplina> findByCodigo(String codigo);
    
    /**
     * Busca disciplinas por nome (case insensitive).
     */
    List<Disciplina> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca disciplinas por professor.
     */
    @Query("SELECT d FROM Disciplina d JOIN d.professores p WHERE p.ID_LATTES = :professorId")
    List<Disciplina> findByProfessorId(@Param("professorId") String professorId);
    
    /**
     * Verifica se existe disciplina com o código informado.
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Busca disciplinas com suas avaliações.
     */
    @Query("SELECT DISTINCT d FROM Disciplina d LEFT JOIN FETCH d.avaliacoes WHERE d.id = :disciplinaId")
    Optional<Disciplina> findByIdWithAvaliacoes(@Param("disciplinaId") Long disciplinaId);
    
    /**
     * Busca disciplinas com seus professores.
     */
    @Query("SELECT DISTINCT d FROM Disciplina d LEFT JOIN FETCH d.professores WHERE d.codigo = :codigo")
    Optional<Disciplina> findByCodigoWithProfessores(@Param("codigo") String codigo);
}
