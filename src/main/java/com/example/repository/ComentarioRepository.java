package com.example.repository;

import com.example.model.Comentario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

/**
 * Repository de Comentario que mantém a interface de negócio e usa JPA internamente.
 * Mantém compatibilidade com a camada de serviço existente.
 */
@Repository
public class ComentarioRepository {
    
    @Autowired
    private ComentarioJpaRepository comentarioJpaRepository;
    
    /**
     * Salva um comentário no banco de dados.
     */
    public Comentario save(Comentario comentario) {
        return comentarioJpaRepository.save(comentario);
    }
    
    /**
     * Busca comentário por ID.
     */
    public Optional<Comentario> findById(Long id) {
        return comentarioJpaRepository.findById(id);
    }
    
    /**
     * Busca todos os comentários.
     */
    public List<Comentario> findAll() {
        return comentarioJpaRepository.findAll();
    }
    
    /**
     * Busca comentários raiz de uma avaliação.
     */
    public ArrayList<Comentario> findComentariosRaizByAvaliacaoId(Long avaliacaoId) {
        return comentarioJpaRepository.findComentariosRaizByAvaliacaoId(avaliacaoId);
    }
    
    /**
     * Busca filhos diretos de um comentário.
     */
    public ArrayList<Comentario> findFilhosByPaiId(Long paiId) {
        return comentarioJpaRepository.findFilhosByPaiId(paiId);
    }
    
    /**
     * Busca comentário com filhos carregados.
     */
    public Optional<Comentario> findByIdWithFilhos(Long id) {
        return comentarioJpaRepository.findByIdWithFilhos(id);
    }
    
    /**
     * Busca comentários de um usuário.
     */
    public ArrayList<Comentario> findByUserEmail(String userEmail) {
        return comentarioJpaRepository.findByUserEmail(userEmail);
    }
    
    /**
     * Busca comentários por texto.
     */
    public ArrayList<Comentario> findByTextoContaining(String texto) {
        return comentarioJpaRepository.findByTextoContaining(texto);
    }
    
    /**
     * Remove comentário por ID.
     */
    public void deleteById(Long id) {
        comentarioJpaRepository.deleteById(id);
    }
    
    /**
     * Remove comentário específico.
     */
    public void delete(Comentario comentario) {
        comentarioJpaRepository.delete(comentario);
    }
    
    /**
     * Conta filhos de um comentário.
     */
    public long countFilhosByPaiId(Long paiId) {
        return comentarioJpaRepository.countFilhosByPaiId(paiId);
    }
    
    /**
     * Verifica se existe comentário por ID.
     */
    public boolean existsById(Long id) {
        return comentarioJpaRepository.existsById(id);
    }
    
    /**
     * Conta total de comentários.
     */
    public long count() {
        return comentarioJpaRepository.count();
    }
    
    /**
     * Busca comentários recentes.
     */
    public ArrayList<Comentario> findRecentComments(int dias) {
        return comentarioJpaRepository.findRecentComments(dias);
    }
}
