package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Avaliacao;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.User;

/**
 * Repository consolidado para Avaliacao que herda diretamente de JpaRepository.
 */
@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
