package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.ArquivoComentario;

/**
 * Repository consolidado para ArquivoComentario que herda diretamente de JpaRepository.
 */
@Repository
public interface ArquivoComentarioRepository extends JpaRepository<ArquivoComentario, Long> {
}
