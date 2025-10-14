package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Avaliacao;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.User;

/**
 * Repository consolidado para Avaliacao que herda diretamente de JpaRepository.
 */
@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    
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
     * Busca avaliação específica de um usuário.
     */
    Optional<Avaliacao> findByProfessorIdAndDisciplinaCodigoAndUserEmail(String professorId, String disciplinaCodigo, String userEmail);
    
    /**
     * Calcula média de notas de um professor.
     */
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.professorId = :professorId")
    Double calcularMediaProfessor(@Param("professorId") String professorId);
    
    /**
     * Calcula média de notas de um professor em uma disciplina.
     */
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.professorId = :professorId AND a.disciplinaCodigo = :disciplinaCodigo")
    Double calcularMediaProfessorDisciplina(@Param("professorId") String professorId, @Param("disciplinaCodigo") String disciplinaCodigo);
    
    /**
     * Busca avaliações por nota.
     */
    ArrayList<Avaliacao> findByNota(Double nota);
    
    /**
     * Busca avaliações com nota maior ou igual.
     */
    ArrayList<Avaliacao> findByNotaGreaterThanEqual(Double nota);
    
    /**
     * Busca avaliações com nota menor ou igual.
     */
    ArrayList<Avaliacao> findByNotaLessThanEqual(Double nota);
    
    /**
     * Conta avaliações por professor.
     */
    long countByProfessorId(String professorId);
    
    /**
     * Conta avaliações por disciplina.
     */
    long countByDisciplinaCodigo(String disciplinaCodigo);
    
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
     * Busca avaliações com comentário por professor e disciplina.
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.professorId = :professorId AND a.disciplinaCodigo = :disciplinaCodigo AND a.comentario IS NOT NULL")
    ArrayList<Avaliacao> findByProfessorAndDisciplinaComComentario(@Param("professorId") String professorId, @Param("disciplinaCodigo") String disciplinaCodigo);
    
    /**
     * Busca avaliações por palavra-chave no comentário.
     */
    @Query("SELECT a FROM Avaliacao a WHERE a.comentario.texto LIKE %:palavra%")
    ArrayList<Avaliacao> findByComentarioTextoContaining(@Param("palavra") String palavra);
    
    /**
     * Conta avaliações com comentário de um professor.
     */
    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.professorId = :professorId AND a.comentario IS NOT NULL")
    long countAvaliacoesComComentarioByProfessor(@Param("professorId") String professorId);
    
    /**
     * Busca avaliação com seu comentário carregado.
     */
    @Query("SELECT a FROM Avaliacao a LEFT JOIN FETCH a.comentario WHERE a.id = :id")
    Optional<Avaliacao> findByIdWithComentario(@Param("id") Long id);
    
    /**
     * Métodos de conveniência implementados como default
     */
    default ArrayList<Avaliacao> findByProfessor(Professor professor) {
        return findByProfessorId(professor.getID_LATTES());
    }
    
    default ArrayList<Avaliacao> findByDisciplina(Disciplina disciplina) {
        return findByDisciplinaCodigo(disciplina.getCodigo());
    }
    
    default ArrayList<Avaliacao> findByUser(User user) {
        return findByUserEmail(user.getEmail());
    }
    
    default ArrayList<Avaliacao> findByProfessorAndDisciplina(Professor professor, Disciplina disciplina) {
        return findByProfessorIdAndDisciplinaCodigo(professor.getID_LATTES(), disciplina.getCodigo());
    }
    
    default Optional<Avaliacao> findByProfessorAndDisciplinaAndUser(Professor professor, Disciplina disciplina, User user) {
        return findByProfessorIdAndDisciplinaCodigoAndUserEmail(
                professor.getID_LATTES(), disciplina.getCodigo(), user.getEmail());
    }
    
    default long countByProfessor(Professor professor) {
        return countByProfessorId(professor.getID_LATTES());
    }
    
    default long countByDisciplina(Disciplina disciplina) {
        return countByDisciplinaCodigo(disciplina.getCodigo());
    }
}
