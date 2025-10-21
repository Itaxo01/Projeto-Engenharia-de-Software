package com.example.service;

import java.util.ArrayList;
import java.util.Optional;

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
        try {
            logger.debug("Salvando professor: {} ({})", professor.getNome(), professor.getProfessorId());
            Professor salvo = professorRepository.save(professor);
            logger.debug("Professor salvo com sucesso: ID {}", salvo.getProfessorId());
            return salvo;
        } catch (Exception e) {
            logger.error("Erro ao salvar professor {}: {}", professor.getProfessorId(), e.getMessage());
            throw e;
        }
    }

	 public Professor criarOuObter(String lattesId, String nome) {
        try {
            logger.debug("=== Processando professor: {} ({}) ===", nome, lattesId);
            
            // Verificar se professor já existe
            Optional<Professor> professorExistente = buscarPorId(lattesId);
            
            if (professorExistente.isPresent()) {
                Professor professor = professorExistente.get();
                logger.debug("*** PROFESSOR EXISTENTE: {} ({}) ***", professor.getNome(), professor.getProfessorId());
                
                // Atualizar nome se necessário
                if (!nome.equals(professor.getNome())) {
                    String nomeAntigo = professor.getNome();
                    professor.setNome(nome);
                    Professor atualizado = professorRepository.save(professor);
                    logger.debug("Nome do professor atualizado: '{}' -> '{}'", nomeAntigo, nome);
                    return atualizado;
                }
                
                return professor;
            }
            
            // Criar novo professor
            Professor novoProfessor = new Professor();
            novoProfessor.setProfessorId(lattesId);
            novoProfessor.setNome(nome);
            
            logger.debug("*** CRIANDO NOVO PROFESSOR: {} ({}) ***", nome, lattesId);
            Professor criado = professorRepository.save(novoProfessor);
            logger.debug("Professor criado com sucesso: {}", criado.toString());
            
            return criado;
            
        } catch (Exception e) {
            logger.error("Erro ao criar/obter professor {} ({}): {}", nome, lattesId, e.getMessage(), e);
            
            // Última tentativa: buscar novamente caso tenha sido criado por outra thread
            try {
                Optional<Professor> ultimaTentativa = professorRepository.findById(lattesId);
                if (ultimaTentativa.isPresent()) {
                    logger.warn("Professor encontrado após erro - possível condição de corrida: {}", 
                               ultimaTentativa.get().getNome());
                    return ultimaTentativa.get();
                }
            } catch (Exception e2) {
                logger.error("Falha na última tentativa de busca: {}", e2.getMessage());
            }
            
            throw e; // Re-lançar se não conseguiu resolver
        }
    }

    // Buscar professor por ID Lattes
    public Optional<Professor> buscarPorId(String id) {
        try {
            Optional<Professor> resultado = professorRepository.findById(id);
            logger.debug("Busca por ID '{}': {}", id, resultado.isPresent() ? "ENCONTRADO" : "NÃO ENCONTRADO");
            return resultado;
        } catch (Exception e) {
            logger.error("Erro ao buscar professor por ID '{}': {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // Buscar todos os professores
    public ArrayList<Professor> buscarTodos() {
        return (ArrayList<Professor>)professorRepository.findAll();
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
}
