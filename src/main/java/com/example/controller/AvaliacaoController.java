package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Professor;
import com.example.model.Usuario;
import com.example.scrapper.DisciplinaScrapper;
import com.example.model.Comentario;
import com.example.model.Avaliacao;
import com.example.model.Disciplina;
import com.example.repository.AvaliacaoRepository;
import com.example.service.ComentarioService;
import com.example.service.AvaliacaoService;
import com.example.service.SessionService;
import com.example.service.DisciplinaService;
import com.example.service.ProfessorService;
import com.example.service.ArquivoComentarioService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exibe detalhes da disciplina via path parameter.
*/
@Controller
@RequestMapping("/api/avaliacao")
public class AvaliacaoController {
	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	@Autowired
	private ComentarioService comentarioService;
	
	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private AvaliacaoService avaliacaoService;

	@Autowired
	private DisciplinaService disciplinaService;

	@Autowired
	private ProfessorService professorService;

	@Autowired
	private ArquivoComentarioService arquivoService;

	private static final Logger logger = LoggerFactory.getLogger(AvaliacaoController.class);


	/**
	 * Endpoint para adicionar/atualizar apenas a NOTA de uma avaliação
	 * Cria uma nova Avaliacao se não existir, ou atualiza a nota se já existir
	 */
	@PostMapping("/rating")
	@ResponseBody
	public ResponseEntity<?> submitRating(@RequestParam("disciplinaId") String disciplinaId, 
													  @RequestParam(value = "professorId", required=false) String professorId,
													  @RequestParam("nota") Integer nota, 
													  HttpServletRequest request) {

		String usuarioEmail = sessionService.getCurrentUser(request);
		
		if (usuarioEmail == null) {
			return ResponseEntity.status(401).body("Usuário não autenticado.");
		}

		try {
			logger.info("Submetendo rating: nota={}, disciplina={}, professor={}, usuario={}", 
						  nota, disciplinaId, professorId, usuarioEmail);
			
			// Validações
			if (nota == null || nota < 1 || nota > 5) {
				return ResponseEntity.status(400).body("Nota deve estar entre 1 e 5.");
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
			if (professorId != null && !professorId.isEmpty() && !professorId.equals("null")) {
				Optional<Professor> professorOpt = professorService.buscarPorId(professorId);
				if (!professorOpt.isPresent()) {
					return ResponseEntity.status(404).body("Professor não encontrado.");
				}
				professor = professorOpt.get();
			}

			// Cria ou atualiza avaliação
			Optional<Avaliacao> avaliacaoOpt = avaliacaoService.create(professor, disciplina, usuario, nota);
			
			if (!avaliacaoOpt.isPresent()) {
				return ResponseEntity.status(500).body("Erro ao salvar avaliação.");
			}

			Avaliacao avaliacao = avaliacaoOpt.get();
			logger.info("Rating salvo com sucesso. Avaliacao ID: {}", avaliacao.getId());

			// ✅ Calcular nova média para retornar ao frontend
			List<Avaliacao> avaliacoesContexto;
			if (professor != null) {
				avaliacoesContexto = avaliacaoRepository.findByDisciplinaAndProfessor(disciplina, professor);
			} else {
				avaliacoesContexto = avaliacaoRepository.findByDisciplinaAndProfessorIsNull(disciplina);
			}
			
			double novaMedia = avaliacoesContexto.stream()
				.filter(a -> a.getNota() != null && a.getNota() > 0)
				.mapToInt(Avaliacao::getNota)
				.average()
				.orElse(0.0);
			
			int totalAvaliacoes = (int) avaliacoesContexto.stream()
				.filter(a -> a.getNota() != null && a.getNota() > 0)
				.count();

			return ResponseEntity.ok(Map.of(
				"success", true,
				"message", "Nota registrada com sucesso",
				"avaliacaoId", avaliacao.getId(),
				"nota", avaliacao.getNota(),
				"novaMedia", novaMedia,
				"totalAvaliacoes", totalAvaliacoes
			));

		} catch (Exception e) {
			logger.error("Erro ao submeter rating: ", e);
			return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
		}
	}

	/**
	 * Endpoint para remover a avaliação (nota) do usuário
	 */
	@PostMapping("/rating/delete")
	@ResponseBody
	public ResponseEntity<?> deleteRating(@RequestParam("disciplinaId") String disciplinaId, 
													  @RequestParam(value = "professorId", required=false) String professorId,
													  HttpServletRequest request) {

		String usuarioEmail = sessionService.getCurrentUser(request);
		
		if (usuarioEmail == null) {
			return ResponseEntity.status(401).body("Usuário não autenticado.");
		}

		try {
			logger.info("Removendo rating: disciplina={}, professor={}, usuario={}", 
						  disciplinaId, professorId, usuarioEmail);
			
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
			if (professorId != null && !professorId.isEmpty() && !professorId.equals("null")) {
				logger.info("Buscando professor para remoção de rating: ID={}", professorId);
				Optional<Professor> professorOpt = professorService.buscarPorId(professorId);
				if (!professorOpt.isPresent()) {
					return ResponseEntity.status(404).body("Professor não encontrado.");
				}
				professor = professorOpt.get();
			}

			// Find and delete the user's rating
			Optional<Avaliacao> avaliacaoOpt;
			if (professor != null) {
				avaliacaoOpt = avaliacaoRepository.findByDisciplinaAndProfessorAndUsuario(disciplina, professor, usuario);
			} else {
				avaliacaoOpt = avaliacaoRepository.findByDisciplinaAndProfessorIsNullAndUsuario(disciplina, usuario);
			}
			
			if (!avaliacaoOpt.isPresent()) {
				return ResponseEntity.status(404).body("Avaliação não encontrada.");
			}

			Avaliacao avaliacao = avaliacaoOpt.get();
			avaliacaoRepository.delete(avaliacao);
			logger.info("Rating removido com sucesso. Avaliacao ID: {}", avaliacao.getId());

			return ResponseEntity.ok(Map.of(
				"success", true,
				"message", "Avaliação removida com sucesso"
			));

		} catch (Exception e) {
			logger.error("Erro ao remover rating: ", e);
			return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
		}
	}

	/**
	 * ✅ Endpoint para criar COMENTÁRIO (agora independente de Avaliacao)
	 * Comentários não são mais únicos por tuple (usuario, disciplina, professor)
	 */
	

}