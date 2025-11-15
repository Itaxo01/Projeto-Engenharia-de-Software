package com.example.repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Avaliacao;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.Usuario;

/**
 * Repository consolidado para Avaliacao que herda diretamente de JpaRepository.
 */
@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

	/**
     * Retorna TODAS as avaliações (ratings) de uma disciplina
     * Comentários agora são carregados separadamente via ComentarioRepository
     */
	@Query("SELECT a FROM Avaliacao a WHERE a.disciplina = :disciplina ORDER BY a.createdAt DESC")
	List<Avaliacao> findAllAvaliacoesByDisciplina(@Param("disciplina") Disciplina disciplina);
     
     Optional<Avaliacao> findByProfessorAndDisciplinaAndUsuario(Professor professor, Disciplina disciplina, Usuario usuario);
     Optional<Avaliacao> findByProfessorIsNullAndDisciplinaAndUsuario(Disciplina disciplina, Usuario usuario);

	 /**
	  * Retorna avaliações de um professor específico em uma disciplina
	  */
	 List<Avaliacao> findByDisciplinaAndProfessor(Disciplina disciplina, Professor professor);
	 
	 /**
	  * Retorna avaliações da disciplina (sem professor)
	  */
	 List<Avaliacao> findByDisciplinaAndProfessorIsNull(Disciplina disciplina);


}
