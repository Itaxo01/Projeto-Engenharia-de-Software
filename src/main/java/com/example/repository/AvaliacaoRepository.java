package com.example.repository;

import com.example.model.Avaliacao;
import com.example.model.Professor;
import com.example.model.Disciplina;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    public ArrayList<Avaliacao> findByProfessor(Professor professor) {
        return avaliacaoJpaRepository.findByProfessorId(professor.getID_LATTES());
    }
    
    /**
     * Busca avaliações por disciplina.
     */
    public ArrayList<Avaliacao> findByDisciplina(Disciplina disciplina) {
        return avaliacaoJpaRepository.findByDisciplinaCodigo(disciplina.getCodigo());
    }
    
    /**
     * Busca avaliações por usuário.
     */
    public ArrayList<Avaliacao> findByUser(User user) {
        return avaliacaoJpaRepository.findByUserEmail(user.getEmail());
    }
    
    /**
     * Busca avaliações por professor e disciplina.
     */
    public ArrayList<Avaliacao> findByProfessorAndDisciplina(Professor professor, Disciplina disciplina) {
        return avaliacaoJpaRepository.findByProfessorIdAndDisciplinaCodigo(professor.getID_LATTES(), disciplina.getCodigo());
    }
    
    /**
     * Busca avaliação específica de um usuário.
     */
    public Optional<Avaliacao> findByProfessorAndDisciplinaAndUser(Professor professor, Disciplina disciplina, User user) {
        return avaliacaoJpaRepository.findByProfessorIdAndDisciplinaCodigoAndUserEmail(
                professor.getID_LATTES(), disciplina.getCodigo(), user.getEmail());
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
    public ArrayList<Avaliacao> findByNota(Integer nota) {
        return avaliacaoJpaRepository.findByNota(nota);
    }
    
    /**
     * Busca avaliações com nota maior ou igual.
     */
    public ArrayList<Avaliacao> findByNotaMaiorOuIgual(Integer nota) {
        return avaliacaoJpaRepository.findByNotaGreaterThanEqual(nota);
    }
    
    /**
     * Busca avaliações com nota menor ou igual.
     */
    public ArrayList<Avaliacao> findByNotaMenorOuIgual(Integer nota) {
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
        return avaliacaoJpaRepository.countByProfessorId(professor.getID_LATTES());
    }
    
    /**
     * Conta avaliações por disciplina.
     */
    public long countByDisciplina(Disciplina disciplina) {
        return avaliacaoJpaRepository.countByDisciplinaCodigo(disciplina.getCodigo());
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
    
    /**
     * Busca avaliações que possuem comentário principal.
     */
    public ArrayList<Avaliacao> findAvaliacoesComComentario() {
        return avaliacaoJpaRepository.findAvaliacoesComComentario();
    }
    
    /**
     * Busca avaliações sem comentário principal.
     */
    public ArrayList<Avaliacao> findAvaliacoesSemComentario() {
        return avaliacaoJpaRepository.findAvaliacoesSemComentario();
    }
    
    /**
     * Busca avaliações com comentário por professor e disciplina.
     */
    public ArrayList<Avaliacao> findByProfessorAndDisciplinaComComentario(Professor professor, Disciplina disciplina) {
        return avaliacaoJpaRepository.findByProfessorAndDisciplinaComComentario(professor.getID_LATTES(), disciplina.getCodigo());
    }
    
    /**
     * Busca avaliações por palavra-chave no comentário.
     */
    public ArrayList<Avaliacao> findByComentarioContendo(String palavra) {
        return avaliacaoJpaRepository.findByComentarioTextoContaining(palavra);
    }
    
    /**
     * Conta avaliações com comentário de um professor.
     */
    public long countAvaliacoesComComentarioByProfessor(Professor professor) {
        return avaliacaoJpaRepository.countAvaliacoesComComentarioByProfessor(professor.getID_LATTES());
    }
    
    /**
     * Busca avaliação com seu comentário carregado.
     */
    public Optional<Avaliacao> findByIdWithComentario(Long id) {
        return avaliacaoJpaRepository.findByIdWithComentario(id);
    }
}
