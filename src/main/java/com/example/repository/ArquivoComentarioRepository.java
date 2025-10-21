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
}
