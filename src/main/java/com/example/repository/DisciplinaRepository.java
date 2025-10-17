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

	 /* Retorna o matching de disciplinas pelo código ou nome 
	  * Ordena os resultados priorizando:
	  * 1. Código exato (case insensitive)
	  * 2. Código que começa com a query (case insensitive)
	  * 3. Nome que começa com a query (case insensitive)
	  * 4. Outros resultados que contenham a query (case insensitive)
	  * Dentro de cada grupo, ordena por código em ordem alfabética
	 */

    @Query("SELECT d FROM Disciplina d WHERE LOWER(d.codigo) LIKE LOWER(:query) OR LOWER(d.nome) LIKE LOWER(:query) " +
            " ORDER BY " +
            " CASE " +
            "  WHEN LOWER(d.codigo) = LOWER(:exactQuery) THEN 1 " +
            "  WHEN LOWER(d.codigo) LIKE LOWER(:startsWithQuery) THEN 2 " +
            "  WHEN LOWER(d.nome) LIKE LOWER(:startsWithQuery) THEN 3 " +
            "  ELSE 4 " +
            " END, " +
            " d.codigo ASC")
    ArrayList<Disciplina> findByCodigoOrNomeContaining(@Param("query") String query,
                                                      @Param("exactQuery") String exactQuery,
                                                      @Param("startsWithQuery") String startsWithQuery);
}