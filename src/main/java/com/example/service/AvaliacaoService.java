package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Avaliacao;
import com.example.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    // Criar nova avaliação
    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    // Buscar avaliação por ID
    public Optional<Avaliacao> buscarPorId(Long id) {
        return avaliacaoRepository.findById(id);
    }

    // Buscar todas as avaliações
    public List<Avaliacao> buscarTodas() {
        return avaliacaoRepository.findAll();
    }

    // Calcular média por professor
    public Double calcularMediaProfessor(String professorId) {
        return avaliacaoRepository.calcularMediaProfessor(professorId);
    }

    // Calcular média por professor e disciplina
    public Double calcularMediaProfessorDisciplina(String professorId, String disciplinaCodigo) {
        return avaliacaoRepository.calcularMediaProfessorDisciplina(professorId, disciplinaCodigo);
    }

    // Deletar avaliação
    public void deletar(Long id) {
        avaliacaoRepository.deleteById(id);
    }

    // Verificar se existe avaliação
    public boolean existe(Long id) {
        return avaliacaoRepository.existsById(id);
    }

    // Contar total de avaliações
    public long contar() {
        return avaliacaoRepository.count();
    }

    // Buscar avaliações com comentário
    public ArrayList<Avaliacao> buscarComComentario() {
        return avaliacaoRepository.findAvaliacoesComComentario();
    }

    // Buscar avaliações sem comentário
    public ArrayList<Avaliacao> buscarSemComentario() {
        return avaliacaoRepository.findAvaliacoesSemComentario();
    }
}
