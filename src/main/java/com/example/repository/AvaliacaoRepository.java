package com.example.repository;

import com.example.model.Avaliacao;
import com.example.model.Professor;
import com.example.model.Disciplina;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Avaliacao que mantém a interface de negócio e usa JPA internamente.
 * Mantém compatibilidade com a camada de serviço existente.
 */
@Repository
public class AvaliacaoRepository {
    
    @Autowired
    private AvaliacaoJpaRepository avaliacaoJpaRepository;
    
    /**
     * Salva uma avaliação no banco de dados.
     */
    public Avaliacao save(Avaliacao avaliacao) {
        return avaliacaoJpaRepository.save(avaliacao);
    }
    
    /**
     * Busca avaliação por ID.
     */
    public Optional<Avaliacao> findById(Long id) {
        return avaliacaoJpaRepository.findById(id);
    }
    
    /**
     * Busca todas as avaliações.
     */
    public List<Avaliacao> findAll() {
        return avaliacaoJpaRepository.findAll();
    }
    
    /**
     * Busca avaliações por professor.
     */
    public List<Avaliacao> findByProfessor(Professor professor) {
        return avaliacaoJpaRepository.findByProfessor(professor);
    }
    
    /**
     * Busca avaliações por disciplina.
     */
    public List<Avaliacao> findByDisciplina(Disciplina disciplina) {
        return avaliacaoJpaRepository.findByDisciplina(disciplina);
    }
    
    /**
     * Busca avaliações por usuário.
     */
    public List<Avaliacao> findByUser(User user) {
        return avaliacaoJpaRepository.findByUser(user);
    }
    
    /**
     * Busca avaliações por professor e disciplina.
     */
    public List<Avaliacao> findByProfessorAndDisciplina(Professor professor, Disciplina disciplina) {
        return avaliacaoJpaRepository.findByProfessorAndDisciplina(professor, disciplina);
    }
    
    /**
     * Busca avaliação específica de um usuário.
     */
    public Optional<Avaliacao> findByProfessorAndDisciplinaAndUser(Professor professor, Disciplina disciplina, User user) {
        return avaliacaoJpaRepository.findByProfessorAndDisciplinaAndUser(professor, disciplina, user);
    }
    
    /**
     * Calcula média de notas de um professor.
     */
    public Double calcularMediaProfessor(String professorId) {
        return avaliacaoJpaRepository.calcularMediaProfessor(professorId);
    }
    
    /**
     * Calcula média de notas de um professor em uma disciplina.
     */
    public Double calcularMediaProfessorDisciplina(String professorId, String disciplinaCodigo) {
        return avaliacaoJpaRepository.calcularMediaProfessorDisciplina(professorId, disciplinaCodigo);
    }
    
    /**
     * Busca avaliações por nota.
     */
    public List<Avaliacao> findByNota(Integer nota) {
        return avaliacaoJpaRepository.findByNota(nota);
    }
    
    /**
     * Busca avaliações com nota maior ou igual.
     */
    public List<Avaliacao> findByNotaMaiorOuIgual(Integer nota) {
        return avaliacaoJpaRepository.findByNotaGreaterThanEqual(nota);
    }
    
    /**
     * Busca avaliações com nota menor ou igual.
     */
    public List<Avaliacao> findByNotaMenorOuIgual(Integer nota) {
        return avaliacaoJpaRepository.findByNotaLessThanEqual(nota);
    }
    
    /**
     * Remove avaliação por ID.
     */
    public void deleteById(Long id) {
        avaliacaoJpaRepository.deleteById(id);
    }
    
    /**
     * Remove avaliação específica.
     */
    public void delete(Avaliacao avaliacao) {
        avaliacaoJpaRepository.delete(avaliacao);
    }
    
    /**
     * Conta avaliações por professor.
     */
    public long countByProfessor(Professor professor) {
        return avaliacaoJpaRepository.countByProfessor(professor);
    }
    
    /**
     * Conta avaliações por disciplina.
     */
    public long countByDisciplina(Disciplina disciplina) {
        return avaliacaoJpaRepository.countByDisciplina(disciplina);
    }
    
    /**
     * Busca avaliação com seus comentários carregados.
     */
    public Optional<Avaliacao> findByIdWithComentarios(Long id) {
        return avaliacaoJpaRepository.findByIdWithComentarios(id);
    }
    
    /**
     * Verifica se existe avaliação por ID.
     */
    public boolean existsById(Long id) {
        return avaliacaoJpaRepository.existsById(id);
    }
    
    /**
     * Conta total de avaliações.
     */
    public long count() {
        return avaliacaoJpaRepository.count();
    }
}
