package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Comentario;

/**
 * Repository consolidado para Comentario que herda diretamente de JpaRepository.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    
    /**
     * Busca comentários raiz (sem pai) de uma avaliação.
     */
    @Query("SELECT c FROM Comentario c WHERE c.avaliacao.id = :avaliacaoId AND c.pai IS NULL")
    ArrayList<Comentario> findComentariosRaizByAvaliacaoId(@Param("avaliacaoId") Long avaliacaoId);
    
    /**
     * Busca filhos diretos de um comentário.
     */
    @Query("SELECT c FROM Comentario c WHERE c.pai.id = :paiId ORDER BY c.createdAt ASC")
    ArrayList<Comentario> findFilhosByPaiId(@Param("paiId") Long paiId);
    
    /**
     * Busca comentário com seus filhos carregados.
     */
    @Query("SELECT c FROM Comentario c LEFT JOIN FETCH c.filhos WHERE c.id = :id")
    Optional<Comentario> findByIdWithFilhos(@Param("id") Long id);
    
    /**
     * Busca comentário com seus arquivos carregados.
     */
    @Query("SELECT c FROM Comentario c LEFT JOIN FETCH c.arquivos WHERE c.id = :id")
    Optional<Comentario> findByIdWithArquivos(@Param("id") Long id);
    
    /**
     * Busca comentário com tudo carregado (filhos e arquivos).
     */
    @Query("SELECT c FROM Comentario c LEFT JOIN FETCH c.filhos LEFT JOIN FETCH c.arquivos WHERE c.id = :id")
    Optional<Comentario> findByIdWithAllData(@Param("id") Long id);
    
    /**
     * Busca comentários de um usuário específico.
     */
    @Query("SELECT c FROM Comentario c WHERE c.usuario.email = :userEmail ORDER BY c.createdAt DESC")
    ArrayList<Comentario> findByUserEmail(@Param("userEmail") String userEmail);
    
    /**
     * Busca comentários que contêm determinado texto.
     */
    @Query("SELECT c FROM Comentario c WHERE LOWER(c.texto) LIKE LOWER(CONCAT('%', :texto, '%'))")
    ArrayList<Comentario> findByTextoContaining(@Param("texto") String texto);
    
    /**
     * Conta filhos diretos de um comentário.
     */
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.pai.id = :paiId")
    long countFilhosByPaiId(@Param("paiId") Long paiId);
    
    /**
     * Conta comentários raiz de uma avaliação.
     */
    @Query("SELECT COUNT(c) FROM Comentario c WHERE c.avaliacao.id = :avaliacaoId AND c.pai IS NULL")
    long countComentariosRaizByAvaliacaoId(@Param("avaliacaoId") Long avaliacaoId);
    
    /**
     * Busca comentários recentes (últimos N dias).
     */
    @Query("SELECT c FROM Comentario c WHERE c.createdAt >= CURRENT_TIMESTAMP - :dias DAY ORDER BY c.createdAt DESC")
    ArrayList<Comentario> findRecentComments(@Param("dias") int dias);
}
