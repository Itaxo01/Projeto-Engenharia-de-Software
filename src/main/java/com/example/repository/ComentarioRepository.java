package com.example.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.model.Professor;

/**
 * Repository para Comentario - agora independente de Avaliacao.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

	// ✅ Buscar comentários principais (sem pai) de uma disciplina (sem professor)
	@Query("SELECT c FROM Comentario c WHERE c.disciplina = :disciplina AND c.professor IS NULL AND c.pai IS NULL")
	List<Comentario> findByDisciplinaAndProfessorIsNullAndPaiIsNull(@Param("disciplina") Disciplina disciplina);
	
	// ✅ Buscar comentários principais (sem pai) de um professor específico
	@Query("SELECT c FROM Comentario c WHERE c.disciplina = :disciplina AND c.professor = :professor AND c.pai IS NULL")
	List<Comentario> findByDisciplinaAndProfessorAndPaiIsNull(@Param("disciplina") Disciplina disciplina, @Param("professor") Professor professor);
}
