package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.Avaliacao;
import com.example.model.Comentario;
import com.example.model.User;
import com.example.repository.ComentarioRepository;

@Service
public class ComentarioService {
    
    @Autowired
    private ComentarioRepository comentarioRepository;
    
    public Comentario criarComentario(User usuario, String texto) {
        Comentario comentario = new Comentario(usuario, texto);
        return comentarioRepository.save(comentario);
    }
    
    public Comentario responderComentario(User usuario, String texto, Long parentId) {
        Comentario parent = comentarioRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário pai não encontrado"));
        
        Comentario resposta = new Comentario(usuario, texto, parent);
        return comentarioRepository.save(resposta);
    }
}