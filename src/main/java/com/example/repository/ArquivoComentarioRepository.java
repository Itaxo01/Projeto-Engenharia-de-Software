package com.example.repository;

import com.example.model.ArquivoComentario;
import com.example.model.Comentario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository de ArquivoComentario que mantém a interface de negócio e usa JPA internamente.
 * Mantém compatibilidade com a camada de serviço existente.
 */
@Repository
public class ArquivoComentarioRepository {
    
    @Autowired
    private ArquivoComentarioJpaRepository arquivoComentarioJpaRepository;
    
    /**
     * Salva um arquivo de comentário no banco de dados.
     */
    public ArquivoComentario save(ArquivoComentario arquivoComentario) {
        return arquivoComentarioJpaRepository.save(arquivoComentario);
    }
    
    /**
     * Busca arquivo por ID.
     */
    public Optional<ArquivoComentario> findById(Long id) {
        return arquivoComentarioJpaRepository.findById(id);
    }
    
    /**
     * Busca todos os arquivos.
     */
    public List<ArquivoComentario> findAll() {
        return arquivoComentarioJpaRepository.findAll();
    }
    
    /**
     * Busca arquivos por comentário.
     */
    public ArrayList<ArquivoComentario> findByComentario(Comentario comentario) {
        return arquivoComentarioJpaRepository.findByComentario(comentario);
    }
    
    /**
     * Busca arquivos por ID do comentário.
     */
    public ArrayList<ArquivoComentario> findByComentarioId(Long comentarioId) {
        return arquivoComentarioJpaRepository.findByComentarioId(comentarioId);
    }
    
    /**
     * Busca arquivo pelo nome único do arquivo.
     */
    public Optional<ArquivoComentario> findByNomeArquivo(String nomeArquivo) {
        return arquivoComentarioJpaRepository.findByNomeArquivo(nomeArquivo);
    }
    
    /**
     * Busca arquivo pelo caminho do arquivo.
     */
    public Optional<ArquivoComentario> findByCaminhoArquivo(String caminhoArquivo) {
        return arquivoComentarioJpaRepository.findByCaminhoArquivo(caminhoArquivo);
    }
    
    /**
     * Busca arquivos por tipo MIME.
     */
    public ArrayList<ArquivoComentario> findByTipoMime(String tipoMime) {
        return arquivoComentarioJpaRepository.findByTipoMime(tipoMime);
    }
    
    /**
     * Busca arquivos por nome original (busca parcial).
     */
    public ArrayList<ArquivoComentario> findByNomeOriginal(String nomeOriginal) {
        return arquivoComentarioJpaRepository.findByNomeOriginalContainingIgnoreCase(nomeOriginal);
    }
    
    /**
     * Busca arquivos com tamanho maior que o especificado.
     */
    public ArrayList<ArquivoComentario> findByTamanhoMaiorQue(Long tamanho) {
        return arquivoComentarioJpaRepository.findByTamanhoGreaterThan(tamanho);
    }
    
    /**
     * Remove arquivo por ID.
     */
    public void deleteById(Long id) {
        arquivoComentarioJpaRepository.deleteById(id);
    }
    
    /**
     * Remove arquivo específico.
     */
    public void delete(ArquivoComentario arquivoComentario) {
        arquivoComentarioJpaRepository.delete(arquivoComentario);
    }
    
    /**
     * Remove todos os arquivos de um comentário.
     */
    public void deleteByComentarioId(Long comentarioId) {
        arquivoComentarioJpaRepository.deleteByComentarioId(comentarioId);
    }
    
    /**
     * Conta total de arquivos por comentário.
     */
    public long countByComentarioId(Long comentarioId) {
        return arquivoComentarioJpaRepository.countByComentarioId(comentarioId);
    }
    
    /**
     * Conta total de arquivos.
     */
    public long count() {
        return arquivoComentarioJpaRepository.count();
    }
    
    /**
     * Verifica se existe arquivo pelo nome único.
     */
    public boolean existsByNomeArquivo(String nomeArquivo) {
        return arquivoComentarioJpaRepository.findByNomeArquivo(nomeArquivo).isPresent();
    }
    
    /**
     * Verifica se existe arquivo pelo caminho.
     */
    public boolean existsByCaminhoArquivo(String caminhoArquivo) {
        return arquivoComentarioJpaRepository.findByCaminhoArquivo(caminhoArquivo).isPresent();
    }
}
