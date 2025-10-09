package com.example.repository;

import com.example.model.Disciplina;
import com.example.model.Professor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Disciplina que mantém a interface de negócio e usa JPA internamente.
 * Mantém compatibilidade com a camada de serviço existente.
 */
@Repository
public class DisciplinaRepository {
    
    @Autowired
    private DisciplinaJpaRepository disciplinaJpaRepository;
    
    /**
     * Salva uma disciplina no banco de dados.
     */
    public Disciplina save(Disciplina disciplina) {
        return disciplinaJpaRepository.save(disciplina);
    }
    
    /**
     * Busca disciplina pelo código.
     */
    public Optional<Disciplina> findByCodigo(String codigo) {
        return disciplinaJpaRepository.findByCodigo(codigo);
    }
    
    /**
     * Busca todas as disciplinas.
     */
    public List<Disciplina> findAll() {
        return disciplinaJpaRepository.findAll();
    }
    
    /**
     * Busca disciplina por ID.
     */
    public Optional<Disciplina> findById(Long id) {
        return disciplinaJpaRepository.findById(id);
    }
    
    /**
     * Busca disciplinas por nome (busca parcial).
     */
    public List<Disciplina> findByNome(String nome) {
        return disciplinaJpaRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Busca disciplinas de um professor específico.
     */
    public List<Disciplina> findByProfessor(String professorId) {
        return disciplinaJpaRepository.findByProfessorId(professorId);
    }
    
    /**
     * Remove disciplina pelo código.
     */
    public void deleteByCodigo(String codigo) {
        disciplinaJpaRepository.findByCodigo(codigo)
            .ifPresent(disciplina -> disciplinaJpaRepository.delete(disciplina));
    }
    
    /**
     * Remove disciplina por ID.
     */
    public void deleteById(Long id) {
        disciplinaJpaRepository.deleteById(id);
    }
    
    /**
     * Verifica se existe disciplina com o código.
     */
    public boolean existsByCodigo(String codigo) {
        return disciplinaJpaRepository.existsByCodigo(codigo);
    }
    
    /**
     * Busca disciplina com suas avaliações carregadas.
     */
    public Optional<Disciplina> findByIdWithAvaliacoes(Long id) {
        return disciplinaJpaRepository.findByIdWithAvaliacoes(id);
    }
    
    /**
     * Busca disciplina com seus professores carregados.
     */
    public Optional<Disciplina> findByCodigoWithProfessores(String codigo) {
        return disciplinaJpaRepository.findByCodigoWithProfessores(codigo);
    }
    
    /**
     * Conta total de disciplinas.
     */
    public long count() {
        return disciplinaJpaRepository.count();
    }
}
