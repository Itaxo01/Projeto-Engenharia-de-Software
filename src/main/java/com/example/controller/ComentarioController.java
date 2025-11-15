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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Professor;
import com.example.model.Usuario;
import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.service.ComentarioService;
import com.example.service.DisciplinaService;
import com.example.service.ProfessorService;
import com.example.service.ArquivoComentarioService;
import com.example.service.UserService;
import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exibe detalhes da disciplina via path parameter.
*/
@Controller
public class ComentarioController {
	@Autowired
	private SessionService sessionService;

	@Autowired 
	private UserService userService;

	@Autowired
	private DisciplinaService disciplinaService;

	@Autowired
	private ArquivoComentarioService arquivoService;

	@Autowired
	private ProfessorService professorService;

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

	@PostMapping("/api/comentarios/{id}/delete")
	@ResponseBody
	public ResponseEntity<?> delete(@PathVariable("id") Long comentarioId, HttpServletRequest request){
		String userEmail = sessionService.getCurrentUser(request);
		
		if (userEmail == null) {
			return ResponseEntity.status(401).body("Usuário não autenticado.");
		}

		Usuario user = userService.getUser(userEmail);
		if (user == null) {
			return ResponseEntity.status(401).body("Usuário não encontrado.");
		}
		
		Comentario comentario = comentarioService.buscarPorId(comentarioId).orElse(null);
		if (comentario == null) {
			return ResponseEntity.status(404).body("Comentário não encontrado.");
		}

		if(user.getAdmin() || comentario.getUsuario().getUser_email().equals(userEmail)) {
			try {
				comentarioService.softDelete(comentarioId, userEmail);
				return ResponseEntity.ok("Comentário deletado com sucesso.");
			} catch(Exception e) {
				return ResponseEntity.status(500).body("Erro ao deletar comentário: " + e.getMessage());
			}
		} 
		else {
				return ResponseEntity.status(403).body("Permissão negada para deletar este comentário.");
		}
	}

	@PostMapping("/api/avaliacao/comentario")
	@ResponseBody
	public ResponseEntity<?> submitComentario(@RequestParam("texto") String texto,
														  @RequestParam("disciplinaId") String disciplinaId, 
														  @RequestParam(value = "professorId", required=false) String professorId,
														  @RequestParam(value = "files", required=false) MultipartFile[] files, 
														  HttpServletRequest request) {

		try {
			logger.info("Iniciando criação de comentário.");
			
			String usuarioEmail = sessionService.getCurrentUser(request);
			if (usuarioEmail == null) {
				return ResponseEntity.status(401).body("Usuário não autenticado.");
			}
			
			Usuario usuario = userService.getUser(usuarioEmail);
			if (usuario == null) {
				return ResponseEntity.status(404).body("Usuário não encontrado.");
			}
			
			Optional<Disciplina> disciplinaOpt = disciplinaService.buscarPorCodigo(disciplinaId);
			if (!disciplinaOpt.isPresent()) {
				return ResponseEntity.status(404).body("Disciplina não encontrada.");
			}
			Disciplina disciplina = disciplinaOpt.get();

			Professor professor = null;
			if (professorId != null && !professorId.isEmpty()) {
				Optional<Professor> professorOpt = professorService.buscarPorId(professorId);
				if (!professorOpt.isPresent()) {
					return ResponseEntity.status(404).body("Professor não encontrado.");
				}
				professor = professorOpt.get();
			}

			if (texto == null || texto.trim().isEmpty()) {
				return ResponseEntity.status(400).body("O texto do comentário não pode ser vazio.");
			}

			if (texto.length() > 2000) {
				return ResponseEntity.status(400).body("O texto do comentário excede o limite de 2000 caracteres.");
			}

			logger.info("Dados validados, criando comentário.");
			// ✅ Criar comentário com disciplina e professor diretamente
			Comentario comentario = comentarioService.criarComentario(usuario, texto, disciplina, professor);
			logger.info("Comentário criado com ID: " + comentario.getComentarioId());

			// Cria os arquivos associados ao comentário, se houver
			if (files != null && files.length > 0) {
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						if (file.getSize() > 5 * 1024 * 1024) { // 5MB
							return ResponseEntity.status(400).body("O arquivo " + file.getOriginalFilename() + " excede o tamanho máximo de 5MB.");
						}

						try {
							arquivoService.salvarArquivo(file, comentario);
							logger.info("Arquivo " + file.getOriginalFilename() + " salvo com sucesso.");
						} catch (Exception e) {
							return ResponseEntity.status(500).body("Erro ao salvar o arquivo " + file.getOriginalFilename() + ": " + e.getMessage());
						}
					}
				}
			}

			logger.info("Comentário salvo com sucesso.");

			return ResponseEntity.ok(Map.of(
				"success", true,
				"message", "Comentário publicado com sucesso",
				"comentarioId", comentario.getComentarioId(),
				"nomeUsuario", usuario.getNome() != null ? usuario.getNome() : "Usuário"
			));

		} catch (Exception e) {
			logger.error("Erro interno: ", e);
			return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
		}
	}
	
}