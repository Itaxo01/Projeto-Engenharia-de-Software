package com.example.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Professor;
import com.example.repository.ProfessorRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProfessorService {

    private static final Logger logger = LoggerFactory.getLogger(ProfessorService.class);

    @Autowired
    private ProfessorRepository professorRepository;

    // Criar novo professor
    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

	 public Professor criarOuObter(String lattesId, String nome) {
        Optional<Professor> professorExistente = buscarPorId(lattesId);
        if (professorExistente.isPresent()) {
            Professor professor = professorExistente.get();
            // Atualizar nome se necessário
            if (!nome.equals(professor.getNome())) {
                professor.setNome(nome);
                return professorRepository.save(professor);
            }
            return professor;
        } 
        
        // Criar novo professor
        Professor novoProfessor = new Professor();
        novoProfessor.setID_LATTES(lattesId);
        novoProfessor.setNome(nome);
        
        return professorRepository.save(novoProfessor);
    }

    // Buscar professor por ID Lattes
    public Optional<Professor> buscarPorId(String idLattes) {
        return professorRepository.findById(idLattes);
    }

    // Buscar todos os professores
    public List<Professor> buscarTodos() {
        return professorRepository.findAll();
    }

    // Buscar professores por nome
    public ArrayList<Professor> buscarPorNome(String nome) {
        return professorRepository.findByNome(nome);
    }

    // Buscar professor por nome exato
    public Optional<Professor> buscarPorNomeExato(String nome) {
        return professorRepository.findByNomeExato(nome);
    }

    // Buscar professores de uma disciplina
    public ArrayList<Professor> buscarPorDisciplina(String codigoDisciplina) {
        return professorRepository.findByDisciplina(codigoDisciplina);
    }

    // Deletar professor
    public void deletar(String idLattes) {
        professorRepository.deleteById(idLattes);
    }

    // Verificar se existe professor
    public boolean existe(String idLattes) {
        return professorRepository.existsById(idLattes);
    }

    // Contar professores
    public long contar() {
        return professorRepository.count();
    }

    // Buscar professor com disciplinas
    public Optional<Professor> buscarComDisciplinas(String idLattes) {
        return professorRepository.findByIdWithDisciplinas(idLattes);
    }

    // Contar professores de uma disciplina
    public long contarPorDisciplina(String codigoDisciplina) {
        return professorRepository.countByDisciplina(codigoDisciplina);
    }
}
