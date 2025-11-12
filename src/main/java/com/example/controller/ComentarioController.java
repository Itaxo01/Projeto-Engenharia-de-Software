package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Professor;
import com.example.service.ComentarioService;
import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exibe detalhes da disciplina via path parameter.
*/
@Controller
public class ComentarioController {
	@Autowired
	private SessionService sessionService;

	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ComentarioController.class);
	
	@Autowired
	private ComentarioService comentarioService;

	@PostMapping("/class/comentario/addComentarioFilho")
	@ResponseBody
	public ResponseEntity<?> addComentarioFilho(@RequestBody Map<String, Object> payload, HttpServletRequest request) {

		String userEmail = sessionService.getCurrentUser(request);
		Long comentarioPaiId = (Long) payload.get("comentarioPaiId");
		Integer nota = (Integer) payload.get("nota");

		// Esse mapping só cria o comentário filho de outro comentário. Não há relação direta dele com a avaliação
		
		return null;
	}

	@PostMapping("/api/comentarios/{id}/votar")
	@ResponseBody
	public ResponseEntity<?> vote(@PathVariable("id") Long comentarioId, @RequestParam("isUpVote") Boolean isUpVote, HttpServletRequest request){
		String userEmail = sessionService.getCurrentUser(request);
		
		if (userEmail == null) {
			return ResponseEntity.status(401).body("Usuário não autenticado.");
		}

		
		logger.debug("isUpVote recebido: " + isUpVote);
		if (isUpVote == null) {
			return ResponseEntity.status(400).body("isUpVote não recebido.");
		}
		
		logger.debug("Registrando voto para comentário ID " + comentarioId + " por usuário " + userEmail + " como " + (isUpVote ? "upvote" : "downvote"));
		try {
			comentarioService.vote(userEmail, comentarioId, isUpVote);
			
			// Buscar comentário atualizado para retornar contadores
			Optional<com.example.model.Comentario> comentarioOpt = comentarioService.buscarPorId(comentarioId);
			if (comentarioOpt.isEmpty()) {
				return ResponseEntity.status(404).body("Comentário não encontrado.");
			}
			
			com.example.model.Comentario comentario = comentarioOpt.get();
			
			// ✅ Buscar o voto atual do usuário
			Boolean userVote = comentario.getVotes().get(userEmail); // true (upvote), false (downvote), null (no vote)
			
			return ResponseEntity.ok(Map.of(
				"success", true,
				"upVotes", comentario.getUpVotes(),
				"downVotes", comentario.getDownVotes(),
				"userVote", userVote != null ? (userVote ? 1 : -1) : 0, // Enviar 0 se for null
				"message", "Voto registrado com sucesso."
			));
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(404).body(e.getMessage());
		} catch(Exception e) {
			return ResponseEntity.status(500).body("Erro ao registrar voto: " + e.getMessage());
		}
	}
	
}