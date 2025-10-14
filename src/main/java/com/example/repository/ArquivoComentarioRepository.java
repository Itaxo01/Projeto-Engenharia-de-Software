package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.ArquivoComentario;
import com.example.model.Comentario;

/**
 * Repository consolidado para ArquivoComentario que herda diretamente de JpaRepository.
 */
@Repository
public interface ArquivoComentarioRepository extends JpaRepository<ArquivoComentario, Long> {
    
    /**
     * Busca arquivos por comentário.
     */
    ArrayList<ArquivoComentario> findByComentario(Comentario comentario);
    
    /**
     * Busca arquivos por ID do comentário.
     */
    ArrayList<ArquivoComentario> findByComentarioId(Long comentarioId);
    
    /**
     * Busca arquivo pelo nome único do arquivo.
     */
    Optional<ArquivoComentario> findByNomeArquivo(String nomeArquivo);
    
    /**
     * Busca arquivo pelo caminho do arquivo.
     */
    Optional<ArquivoComentario> findByCaminhoArquivo(String caminhoArquivo);
    
    /**
     * Busca arquivos por tipo MIME.
     */
    ArrayList<ArquivoComentario> findByTipoMime(String tipoMime);
    
    /**
     * Busca arquivos com tamanho maior que o especificado.
     */
    @Query("SELECT a FROM ArquivoComentario a WHERE a.tamanho > :tamanho")
    ArrayList<ArquivoComentario> findByTamanhoGreaterThan(@Param("tamanho") Long tamanho);
    
    /**
     * Busca arquivos por nome original (busca parcial).
     */
    ArrayList<ArquivoComentario> findByNomeOriginalContainingIgnoreCase(String nomeOriginal);
    
    /**
     * Conta total de arquivos por comentário.
     */
    long countByComentarioId(Long comentarioId);
    
    /**
     * Remove todos os arquivos de um comentário.
     */
    void deleteByComentarioId(Long comentarioId);
    
    /**
     * Métodos de conveniência implementados como default
     */
    default ArrayList<ArquivoComentario> findByNomeOriginal(String nomeOriginal) {
        return findByNomeOriginalContainingIgnoreCase(nomeOriginal);
    }
    
    default boolean existsByNomeArquivo(String nomeArquivo) {
        return findByNomeArquivo(nomeArquivo).isPresent();
    }
    
    default boolean existsByCaminhoArquivo(String caminhoArquivo) {
        return findByCaminhoArquivo(caminhoArquivo).isPresent();
    }
}
