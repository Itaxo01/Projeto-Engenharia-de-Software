package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Comentario;
import com.example.model.Usuario;
import com.example.repository.ComentarioRepository;

@Service
public class ComentarioService {
    
    @Autowired
    private ComentarioRepository comentarioRepository;
    
    // Criar comentário
    public Comentario criarComentario(Usuario usuario, String texto) {
        Comentario comentario = new Comentario(usuario, texto);
        return comentarioRepository.save(comentario);
    }
    
    // Responder comentário
    public Comentario responderComentario(Usuario usuario, String texto, Long parentId) {
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

	 // Deletar comentário
    public void deletar(Long id) {
        comentarioRepository.deleteById(id);
    }
    
    // Verificar se existe comentário
    public boolean existe(Long id) {
        return comentarioRepository.existsById(id);
    }
}