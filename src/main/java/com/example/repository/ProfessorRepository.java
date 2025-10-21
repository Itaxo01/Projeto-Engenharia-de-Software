package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.model.Professor;

/**
 * Repository consolidado para Professor que herda diretamente de JpaRepository.
 */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, String> {
}
