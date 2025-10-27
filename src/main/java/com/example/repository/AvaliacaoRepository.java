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
     * Retorna TODAS as avaliações de uma disciplina (com/sem professor, com/sem comentário)
     * O front-end processará e separará os dados
     */
	@Query("SELECT a FROM Avaliacao a LEFT JOIN FETCH a.comentario c LEFT JOIN FETCH c.usuario WHERE a.disciplinaId = :disciplinaId ORDER BY a.createdAt DESC")
	List<Avaliacao> findAllAvaliacoesByDisciplina(@Param("disciplinaId") String disciplinaId);
   
}
