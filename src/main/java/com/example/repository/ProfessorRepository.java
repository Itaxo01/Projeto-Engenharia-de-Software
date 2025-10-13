package com.example.repository;

import com.example.model.Professor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository de Professor que mantém a interface de negócio e usa JPA internamente.
 * Mantém compatibilidade com a camada de serviço existente.
 */
@Repository
public class ProfessorRepository {
    
    @Autowired
    private ProfessorJpaRepository professorJpaRepository;
    
    /**
     * Salva um professor no banco de dados.
     */
    public Professor save(Professor professor) {
        return professorJpaRepository.save(professor);
    }
    
    /**
     * Busca professor por ID Lattes.
     */
    public Optional<Professor> findById(String idLattes) {
        return professorJpaRepository.findById(idLattes);
    }
    
    /**
     * Busca todos os professores.
     */
    public List<Professor> findAll() {
        return professorJpaRepository.findAll();
    }
    
    /**
     * Busca professores por nome (busca parcial).
     */
    public ArrayList<Professor> findByNome(String nome) {
        return professorJpaRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Busca professor por nome exato.
     */
    public Optional<Professor> findByNomeExato(String nome) {
        return professorJpaRepository.findByNome(nome);
    }
    
    /**
     * Busca professores que lecionam uma disciplina específica.
     */
    public ArrayList<Professor> findByDisciplina(String codigoDisciplina) {
        return professorJpaRepository.findByDisciplinaCodigo(codigoDisciplina);
    }
    
    /**
     * Remove professor por ID Lattes.
     */
    public void deleteById(String idLattes) {
        professorJpaRepository.deleteById(idLattes);
    }
    
    /**
     * Remove professor específico.
     */
    public void delete(Professor professor) {
        professorJpaRepository.delete(professor);
    }
    
    /**
     * Verifica se existe professor com ID Lattes.
     */
    public boolean existsById(String idLattes) {
        return professorJpaRepository.existsById(idLattes);
    }
    
    /**
     * Busca professor com suas disciplinas carregadas.
     */
    public Optional<Professor> findByIdWithDisciplinas(String idLattes) {
        return professorJpaRepository.findByIdWithDisciplinas(idLattes);
    }
    
    /**
     * Busca professor com suas avaliações carregadas.
     */
    public Optional<Professor> findByIdWithAvaliacoes(String idLattes) {
        return professorJpaRepository.findByIdWithAvaliacoes(idLattes);
    }
    
    /**
     * Conta professores que lecionam uma disciplina.
     */
    public long countByDisciplina(String codigoDisciplina) {
        return professorJpaRepository.countByDisciplinaCodigo(codigoDisciplina);
    }
    
    /**
     * Conta total de professores.
     */
    public long count() {
        return professorJpaRepository.count();
    }
}
