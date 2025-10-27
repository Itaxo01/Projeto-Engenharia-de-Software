package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Comentario;

/**
 * Repository consolidado para Comentario que herda diretamente de JpaRepository.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}
