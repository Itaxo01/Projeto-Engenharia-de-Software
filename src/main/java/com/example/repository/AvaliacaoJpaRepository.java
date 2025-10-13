package com.example.repository;

import com.example.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface JPA Repository para operações de banco de dados da entidade Avaliacao.
 */
@Repository
public interface AvaliacaoJpaRepository extends JpaRepository<Avaliacao, Long> {
    
    /**
     * Busca avaliações por ID do professor.
     */
    ArrayList<Avaliacao> findByProfessorId(String professorId);
    
    /**
     * Busca avaliações por código da disciplina.
     */
    ArrayList<Avaliacao> findByDisciplinaCodigo(String disciplinaCodigo);
    
    /**
     * Busca avaliações por email do usuário.
     */
    ArrayList<Avaliacao> findByUserEmail(String userEmail);
    
    /**
     * Busca avaliações por professor e disciplina.
     */
    ArrayList<Avaliacao> findByProfessorIdAndDisciplinaCodigo(String professorId, String disciplinaCodigo);
    
    /**
     * Busca avaliação específica de um usuário para professor em disciplina.
     */
    Optional<Avaliacao> findByProfessorIdAndDisciplinaCodigoAndUserEmail(String professorId, String disciplinaCodigo, String userEmail);
    
    /**
     * Calcula média de notas de um professor.
     */
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.professorId = :professorId")
    Double calcularMediaProfessor(@Param("professorId") String professorId);
    
    /**
     * Calcula média de notas de um professor em uma disciplina específica.
     */
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.professorId = :professorId AND a.disciplinaCodigo = :disciplinaCodigo")
    Double calcularMediaProfessorDisciplina(@Param("professorId") String professorId, @Param("disciplinaCodigo") String disciplinaCodigo);
    
    /**
     * Conta avaliações por professor.
     */
    long countByProfessorId(String professorId);
    
    /**
     * Conta avaliações por disciplina.
     */
    long countByDisciplinaCodigo(String disciplinaCodigo);
    
    /**
     * Busca avaliações com comentário carregado.
     */
    @Query("SELECT DISTINCT a FROM Avaliacao a LEFT JOIN FETCH a.comentario WHERE a.id = :avaliacaoId")
    Optional<Avaliacao> findByIdWithComentario(@Param("avaliacaoId") Long avaliacaoId);
    
    /**
     * Busca avaliações por nota específica.
     */
    ArrayList<Avaliacao> findByNota(Integer nota);
    
    /**
     * Busca avaliações com nota maior ou igual.
     */
    ArrayList<Avaliacao> findByNotaGreaterThanEqual(Integer nota);
    
    /**
     * Busca avaliações com nota menor ou igual.
     */
    ArrayList<Avaliacao> findByNotaLessThanEqual(Integer nota);
    
    /**
     * Busca avaliações que possuem comentário principal.
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.comentario IS NOT NULL")
    ArrayList<Avaliacao> findAvaliacoesComComentario();
    
    /**
     * Busca avaliações sem comentário principal.
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.comentario IS NULL")
    ArrayList<Avaliacao> findAvaliacoesSemComentario();
    
    /**
     * Busca avaliações por professor e disciplina que possuem comentário.
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.professorId = :professorId AND a.disciplinaCodigo = :disciplinaCodigo AND a.comentario IS NOT NULL")
    ArrayList<Avaliacao> findByProfessorAndDisciplinaComComentario(@Param("professorId") String professorId, @Param("disciplinaCodigo") String disciplinaCodigo);
    
    /**
     * Busca avaliações por palavra-chave no texto do comentário.
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.comentario IS NOT NULL AND LOWER(a.comentario.texto) LIKE LOWER(CONCAT('%', :palavra, '%'))")
    ArrayList<Avaliacao> findByComentarioTextoContaining(@Param("palavra") String palavra);
    
    /**
     * Conta avaliações com comentário por professor.
     */
    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.professorId = :professorId AND a.comentario IS NOT NULL")
    long countAvaliacoesComComentarioByProfessor(@Param("professorId") String professorId);
}
