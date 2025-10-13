package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.Avaliacao;
import com.example.model.Comentario;
import com.example.model.User;
import com.example.repository.ComentarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {
    
    @Autowired
    private ComentarioRepository comentarioRepository;
    
    // Criar comentário
    public Comentario criarComentario(User usuario, String texto) {
        Comentario comentario = new Comentario(usuario, texto);
        return comentarioRepository.save(comentario);
    }
    
    // Responder comentário
    public Comentario responderComentario(User usuario, String texto, Long parentId) {
        Comentario parent = comentarioRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário pai não encontrado"));
        
        Comentario resposta = new Comentario(usuario, texto, parent);
        return comentarioRepository.save(resposta);
    }
    
    // Buscar comentário por ID
    public Optional<Comentario> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }
    
    // Buscar todos os comentários
    public List<Comentario> buscarTodos() {
        return comentarioRepository.findAll();
    }
    
    // Buscar comentários raiz de uma avaliação
    public ArrayList<Comentario> buscarComentariosRaiz(Long avaliacaoId) {
        return comentarioRepository.findComentariosRaizByAvaliacaoId(avaliacaoId);
    }
    
    // Buscar filhos de um comentário
    public ArrayList<Comentario> buscarFilhos(Long paiId) {
        return comentarioRepository.findFilhosByPaiId(paiId);
    }
    
    // Buscar comentários de um usuário
    public ArrayList<Comentario> buscarPorUsuario(String userEmail) {
        return comentarioRepository.findByUserEmail(userEmail);
    }
    
    // Buscar comentários por texto
    public ArrayList<Comentario> buscarPorTexto(String texto) {
        return comentarioRepository.findByTextoContaining(texto);
    }
    
    // Deletar comentário
    public void deletar(Long id) {
        comentarioRepository.deleteById(id);
    }
    
    // Contar filhos de um comentário
    public long contarFilhos(Long paiId) {
        return comentarioRepository.countFilhosByPaiId(paiId);
    }
    
    // Verificar se existe comentário
    public boolean existe(Long id) {
        return comentarioRepository.existsById(id);
    }
    
    // Buscar comentários recentes
    public ArrayList<Comentario> buscarRecentes(int dias) {
        return comentarioRepository.findRecentComments(dias);
    }
}