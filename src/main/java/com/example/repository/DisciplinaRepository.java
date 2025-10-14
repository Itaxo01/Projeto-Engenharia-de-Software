package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Disciplina;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    // MÉTODO CRÍTICO - verificar se está funcionando
    @Query("SELECT d FROM Disciplina d WHERE d.codigo = :codigo")
    Optional<Disciplina> findByCodigo(@Param("codigo") String codigo);

    // Método alternativo para debug
    @Query("SELECT d FROM Disciplina d WHERE UPPER(d.codigo) = UPPER(:codigo)")
    Optional<Disciplina> findByCodigoIgnoreCase(@Param("codigo") String codigo);

    ArrayList<Disciplina> findByNome(String nome);

    @Query("SELECT d FROM Disciplina d WHERE :professorId MEMBER OF d.professores")
    ArrayList<Disciplina> findByProfessor(@Param("professorId") String professorId);

    void deleteByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    @Query("SELECT d FROM Disciplina d WHERE d.codigo = :codigo")
    Optional<Disciplina> findByCodigoWithProfessores(@Param("codigo") String codigo);
}