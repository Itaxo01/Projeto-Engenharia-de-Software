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

    // Buscar arquivos por comentário
    public ArrayList<ArquivoComentario> buscarPorComentario(Comentario comentario) {
        return arquivoComentarioRepository.findByComentario(comentario);
    }

    // Buscar arquivos por ID do comentário
    public ArrayList<ArquivoComentario> buscarPorComentarioId(Long comentarioId) {
        return arquivoComentarioRepository.findByComentarioId(comentarioId);
    }

    // Buscar arquivo por nome único
    public Optional<ArquivoComentario> buscarPorNomeArquivo(String nomeArquivo) {
        return arquivoComentarioRepository.findByNomeArquivo(nomeArquivo);
    }

    // Buscar arquivo por caminho
    public Optional<ArquivoComentario> buscarPorCaminho(String caminho) {
        return arquivoComentarioRepository.findByCaminhoArquivo(caminho);
    }

    // Buscar arquivos por tipo MIME
    public ArrayList<ArquivoComentario> buscarPorTipoMime(String tipoMime) {
        return arquivoComentarioRepository.findByTipoMime(tipoMime);
    }

    // Buscar arquivos por nome original
    public ArrayList<ArquivoComentario> buscarPorNomeOriginal(String nomeOriginal) {
        return arquivoComentarioRepository.findByNomeOriginal(nomeOriginal);
    }

    // Deletar arquivo
    public void deletar(Long id) {
        arquivoComentarioRepository.deleteById(id);
    }

    // Deletar arquivos de um comentário
    public void deletarPorComentario(Long comentarioId) {
        arquivoComentarioRepository.deleteByComentarioId(comentarioId);
    }

    // Contar arquivos de um comentário
    public long contarPorComentario(Long comentarioId) {
        return arquivoComentarioRepository.countByComentarioId(comentarioId);
    }

    // Verificar se arquivo existe por nome
    public boolean existePorNome(String nomeArquivo) {
        return arquivoComentarioRepository.existsByNomeArquivo(nomeArquivo);
    }

    // Verificar se arquivo existe por caminho
    public boolean existePorCaminho(String caminho) {
        return arquivoComentarioRepository.existsByCaminhoArquivo(caminho);
    }
}
