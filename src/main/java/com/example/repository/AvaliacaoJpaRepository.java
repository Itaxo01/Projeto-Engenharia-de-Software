package com.example.repository;

import com.example.model.Avaliacao;
import com.example.model.Professor;
import com.example.model.Disciplina;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Interface JPA Repository para operações de banco de dados da entidade Avaliacao.
 */
@Repository
public interface AvaliacaoJpaRepository extends JpaRepository<Avaliacao, Long> {
    
    /**
     * Busca avaliações por professor.
     */
    List<Avaliacao> findByProfessor(Professor professor);
    
    /**
     * Busca avaliações por disciplina.
     */
    List<Avaliacao> findByDisciplina(Disciplina disciplina);
    
    /**
     * Busca avaliações por usuário.
     */
    List<Avaliacao> findByUser(User user);
    
    /**
     * Busca avaliações por professor e disciplina.
     */
    List<Avaliacao> findByProfessorAndDisciplina(Professor professor, Disciplina disciplina);
    
    /**
     * Busca avaliação específica de um usuário para professor em disciplina.
     */
    Optional<Avaliacao> findByProfessorAndDisciplinaAndUser(Professor professor, Disciplina disciplina, User user);
    
    /**
     * Calcula média de notas de um professor.
     */
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.professor.ID_LATTES = :professorId")
    Double calcularMediaProfessor(@Param("professorId") String professorId);
    
    /**
     * Calcula média de notas de um professor em uma disciplina específica.
     */
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.professor.ID_LATTES = :professorId AND a.disciplina.codigo = :disciplinaCodigo")
    Double calcularMediaProfessorDisciplina(@Param("professorId") String professorId, @Param("disciplinaCodigo") String disciplinaCodigo);
    
    /**
     * Conta avaliações por professor.
     */
    long countByProfessor(Professor professor);
    
    /**
     * Conta avaliações por disciplina.
     */
    long countByDisciplina(Disciplina disciplina);
    
    /**
     * Busca avaliações com seus comentários carregados.
     */
    @Query("SELECT DISTINCT a FROM Avaliacao a LEFT JOIN FETCH a.comentarios WHERE a.id = :avaliacaoId")
    Optional<Avaliacao> findByIdWithComentarios(@Param("avaliacaoId") Long avaliacaoId);
    
    /**
     * Busca avaliações por nota específica.
     */
    List<Avaliacao> findByNota(Integer nota);
    
    /**
     * Busca avaliações com nota maior ou igual.
     */
    List<Avaliacao> findByNotaGreaterThanEqual(Integer nota);
    
    /**
     * Busca avaliações com nota menor ou igual.
     */
    List<Avaliacao> findByNotaLessThanEqual(Integer nota);
}
