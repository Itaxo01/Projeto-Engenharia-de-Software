package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.repository.DisciplinaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DisciplinaService {

    private static final Logger logger = LoggerFactory.getLogger(DisciplinaService.class);

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    // Criar nova disciplina
    public Disciplina salvar(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    public Disciplina criarOuAtualizar(String codigo, String nome, Set<Professor> professores) {
        logger.debug("=== Buscando disciplina com código: {} ===", codigo);
        
        // DEBUG: Verificar múltiplas formas
        Optional<Disciplina> disciplinaExistente = buscarPorCodigo(codigo);
        logger.debug("Resultado buscarPorCodigo: {}", disciplinaExistente.isPresent() ? "ENCONTRADA" : "NÃO ENCONTRADA");
        
        // DEBUG: Verificar se existe com método alternativo
        boolean existe = disciplinaRepository.existsByCodigo(codigo);
        logger.debug("Resultado existsByCodigo: {}", existe);
        
        // DEBUG: Buscar todas e verificar manualmente
        List<Disciplina> todas = disciplinaRepository.findAll();
        boolean existeManual = todas.stream().anyMatch(d -> codigo.equals(d.getCodigo()));
        logger.debug("Verificação manual - Total disciplinas: {}, Existe '{}': {}", todas.size(), codigo, existeManual);
        
        Disciplina disciplina;
        if (disciplinaExistente.isPresent()) {
            disciplina = disciplinaExistente.get();
            logger.info("*** DISCIPLINA EXISTENTE ENCONTRADA: {} (ID: {}) ***", codigo, disciplina.getId());
            
            // Atualizar nome se necessário
            if (!nome.equals(disciplina.getNome())) {
                disciplina.setNome(nome);
                logger.debug("Nome da disciplina atualizado de '{}' para '{}'", disciplina.getNome(), nome);
            }
        } else {
            disciplina = new Disciplina();
            disciplina.setCodigo(codigo);
            disciplina.setNome(nome);
            logger.info("*** CRIANDO NOVA DISCIPLINA: {} ***", codigo);
        }
        
        // Adicionar professores
        int professoresAntes = disciplina.getProfessores().size();
        logger.debug("Professores antes: {}", professoresAntes);
        
        for (Professor professor : professores) {
            if (!disciplina.temProfessor(professor)) {
                disciplina.adicionarProfessor(professor);
                logger.debug("Professor {} adicionado à disciplina {}", professor.getNome(), codigo);
            } else {
                logger.debug("Professor {} já estava associado à disciplina {}", professor.getNome(), codigo);
            }
        }
        
        int professoresDepois = disciplina.getProfessores().size();
        logger.info("Disciplina {}: {} professores antes, {} adicionados, {} total", 
                   codigo, professoresAntes, (professoresDepois - professoresAntes), professoresDepois);
        
        Disciplina disciplinaSalva = disciplinaRepository.save(disciplina);
        logger.debug("Disciplina salva com ID: {}", disciplinaSalva.getId());
        
        return disciplinaSalva;
    }

    public Optional<Disciplina> buscarPorCodigo(String codigo) {
        try {
            Optional<Disciplina> resultado = disciplinaRepository.findByCodigo(codigo);
            logger.debug("buscarPorCodigo('{}') -> {}", codigo, resultado.isPresent() ? "ENCONTRADA" : "NÃO ENCONTRADA");
            return resultado;
        } catch (Exception e) {
            logger.error("Erro ao buscar disciplina por código '{}': {}", codigo, e.getMessage());
            return Optional.empty();
        }
    }

    // ... resto dos métodos existentes ...
    
    public Optional<Disciplina> buscarPorId(Long id) {
        return disciplinaRepository.findById(id);
    }

    public List<Disciplina> buscarTodas() {
        return disciplinaRepository.findAll();
    }

    public ArrayList<Disciplina> buscarPorNome(String nome) {
        return disciplinaRepository.findByNome(nome);
    }

    public ArrayList<Disciplina> buscarPorProfessor(String professorId) {
        return disciplinaRepository.findByProfessor(professorId);
    }

    public void deletarPorCodigo(String codigo) {
        disciplinaRepository.deleteByCodigo(codigo);
    }

    public void deletar(Long id) {
        disciplinaRepository.deleteById(id);
    }

    public boolean existePorCodigo(String codigo) {
        return disciplinaRepository.existsByCodigo(codigo);
    }

    public long contar() {
        return disciplinaRepository.count();
    }

    public Optional<Disciplina> buscarComProfessores(String codigo) {
        return disciplinaRepository.findByCodigoWithProfessores(codigo);
    }
}