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
import com.example.model.Usuario;
import com.example.repository.DisciplinaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DisciplinaService {

    private static final Logger logger = LoggerFactory.getLogger(DisciplinaService.class);

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    // Criar nova disciplina
    public Disciplina criarOuAtualizar(String codigo, String nome, Set<Professor> professores) {
        logger.debug("=== Buscando disciplina com código: {} ===", codigo);

        Optional<Disciplina> disciplina = disciplinaRepository.findByCodigo(codigo);
        logger.debug("Resultado existsByCodigo: {}", disciplina.isPresent() ? "ENCONTRADA" : "NÃO ENCONTRADA");
		  Disciplina d;
        if (disciplina.isPresent()) {
			  d = disciplina.get();
			  logger.debug("*** DISCIPLINA EXISTENTE ENCONTRADA: {} (ID: {}) ***", codigo, d.getDisciplinaId());
            // Atualizar nome se necessário
            if (!nome.equals(d.getNome())) {
                d.setNome(nome);
                logger.debug("Nome da disciplina atualizado de '{}' para '{}'", d.getNome(), nome);
            }
        } else {
            d = new Disciplina();
            d.setCodigo(codigo);
            d.setNome(nome);
            logger.info("*** CRIANDO NOVA DISCIPLINA: {} ***", codigo);
        }
        
        // Adicionar professores
        int professoresAntes = d.getProfessores().size();
        logger.debug("Professores antes: {}", professoresAntes);
        
        for (Professor professor : professores) {
            if (!d.temProfessor(professor)) {
                d.adicionarProfessor(professor);
                logger.debug("Professor {} adicionado à disciplina {}", professor.getNome(), codigo);
            } else {
                logger.debug("Professor {} já estava associado à disciplina {}", professor.getNome(), codigo);
            }
        }

        int professoresDepois = d.getProfessores().size();
        logger.debug("Disciplina {}: {} professores antes, {} adicionados, {} total",
                   codigo, professoresAntes, (professoresDepois - professoresAntes), professoresDepois);

        Disciplina disciplinaSalva = disciplinaRepository.save(d);
        logger.debug("Disciplina salva com ID: {}", disciplinaSalva.getDisciplinaId());
        
        return disciplinaSalva;
    }

    @Transactional
    public Optional<Disciplina> buscarPorCodigo(String codigo) {
        try {
            Optional<Disciplina> resultado = disciplinaRepository.findByCodigo(codigo);
            logger.debug("buscarPorCodigo('{}') -> {}", codigo, resultado.isPresent() ? "ENCONTRADA" : "NÃO ENCONTRADA");
            if(resultado.isPresent()) { // Para conseguir os dados dos professores
                resultado.get().getProfessores().size();
            }
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
}