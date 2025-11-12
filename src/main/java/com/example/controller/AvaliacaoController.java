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
public class AvaliacaoController {
	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	@Autowired
	private ComentarioService comentarioService;

	@Autowired
	private AvaliacaoService avaliacaoService;

	@Autowired
	private DisciplinaService disciplinaService;

	@Autowired
	private ProfessorService professorService;

	@Autowired
	private ArquivoComentarioService arquivoService;

	private static final Logger logger = LoggerFactory.getLogger(AvaliacaoController.class);


	@PostMapping("/api/comentario/criar")
	@ResponseBody
	public ResponseEntity<?> criarComentario(@RequestParam("texto") String texto,
														 @RequestParam("disciplinaId") String disciplinaId, 
														 @RequestParam(value = "professorId", required=false) String professorId,
														 @RequestParam("nota") Integer nota,
														 @RequestParam(value = "files", required=false) MultipartFile[] files, HttpServletRequest request) {

      try {
			logger.info("Iniciando criação de comentário e avaliação.");
			
			String usuarioEmail = sessionService.getCurrentUser(request);
			if(usuarioEmail == null) return ResponseEntity.status(403).body("Usuário não autenticado.");
			
			Usuario usuario = userService.getUser(usuarioEmail);
			if(usuario == null) return ResponseEntity.status(403).body("Usuário não encontrado.");
			
			Optional<Disciplina> disciplinaOpt = disciplinaService.buscarPorCodigo(disciplinaId);
			if(!disciplinaOpt.isPresent()) {
				return ResponseEntity.status(400).body("Disciplina não encontrada.");
			}
			Disciplina disciplina = disciplinaOpt.get();

			Professor professor = null;
			if(professorId != null && !professorId.isEmpty()) {
				Optional<Professor> professorOpt = professorService.buscarPorId(professorId);
				if(!professorOpt.isPresent()) {
					return ResponseEntity.status(400).body("Professor não encontrado.");
				}
				professor = professorOpt.get();
			}

			if(texto == null || texto.trim().isEmpty()) {
				return ResponseEntity.status(400).body("O texto do comentário não pode ser vazio.");
			}

			if(texto.length() > 2000) {
				return ResponseEntity.status(400).body("O texto do comentário excede o limite de 2000 caracteres.");
			}

			
			
			logger.info("Dados OK, criando comentário.");
			Comentario comentario = comentarioService.criarComentario(usuario, texto);
			logger.info("Comentário criado com ID: " + comentario.getComentarioId());

			// Cria os arquivos associados ao comentário, se houver
			if (files != null && files.length > 0) {
				for (MultipartFile file : files) {
					if(!file.isEmpty()) {
						if(file.getSize() > 5 * 1024 * 1024) { // 5MB
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
			logger.info("Criando avaliação associada ao comentário.");

			Optional<Avaliacao> avaliacaoOpt = avaliacaoService.create(professor, disciplina, usuario, nota, comentario);
			if(!avaliacaoOpt.isPresent()) {
				return ResponseEntity.status(500).body("Erro ao criar avaliação.");
			}
			// comentario.setAvaliacao(avaliacaoOpt.get());
			// comentarioService.salvar(comentario); // Atualiza o comentário com a avaliação

			logger.info("Avaliação criada/achada com ID: " + avaliacaoOpt.get().getId());

			return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Avaliação criada com sucesso",
                "avaliacaoId", avaliacaoOpt.get().getId(),
                "comentarioId", comentario.getComentarioId(),
                "nomeUsuario", usuario.getNome() != null ? usuario.getNome() : "Usuário"
            ));

		} catch (Exception e) {
            logger.error("Erro interno: ", e);
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
		}
	}

	// A avaliação terá ou comentário ou nota, em primeira instância, visto que os dois serão criados separadamente
	// Ao adicionar uma nota a uma avaliação que já existe, só se modifica a nota
	// mesma coisa para o comentário

	@PostMapping("/class/addNota")
	@ResponseBody
	public ResponseEntity<?> addNota(@RequestParam("texto") String texto,
												@RequestParam("disciplinaId") String disciplinaId, 
												@RequestParam(value = "professorId", required=false) String professorId,
												@RequestParam("nota") Integer nota, HttpServletRequest request) {

		String usuarioEmail = sessionService.getCurrentUser(request);

		try {
			avaliacaoService.addNota(professorId, disciplinaId, usuarioEmail, nota);
			return ResponseEntity.ok("Avaliação adicionada/atualizada com sucesso.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}
	}

}