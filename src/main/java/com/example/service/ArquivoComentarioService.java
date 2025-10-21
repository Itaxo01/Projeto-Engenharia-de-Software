package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.ArquivoComentario;
import com.example.model.Comentario;
import com.example.repository.ArquivoComentarioRepository;

@Service
public class ArquivoComentarioService {

    @Autowired
    private ArquivoComentarioRepository arquivoComentarioRepository;

    // Salvar arquivo
    public ArquivoComentario salvar(ArquivoComentario arquivoComentario) {
        return arquivoComentarioRepository.save(arquivoComentario);
    }

    // Buscar arquivo por ID
    public Optional<ArquivoComentario> buscarPorId(Long id) {
        return arquivoComentarioRepository.findById(id);
    }

    // Buscar todos os arquivos
    public List<ArquivoComentario> buscarTodos() {
        return arquivoComentarioRepository.findAll();
    }

    // Deletar arquivo
    public void deletar(Long id) {
        arquivoComentarioRepository.deleteById(id);
    }
}
