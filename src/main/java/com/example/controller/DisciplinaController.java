package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.service.AvaliacaoService;
import com.example.service.ComentarioService;
import com.example.service.DisciplinaService;
import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Exibe detalhes da disciplina via path parameter.
*/
@Controller
public class DisciplinaController {
	@Autowired
	private SessionService sessionService;

	@Autowired
	private DisciplinaService disciplinaService;

	@Autowired
	private AvaliacaoService avaliacaoService;

	@Autowired
	private ComentarioService comentarioService;

	private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DisciplinaController.class);
	
	// Aqui o id utilizado é o mesmo que o codigo utilizado no banco de dados
	@GetMapping("/class/{id}")
	public String classDetails(HttpServletRequest request, @PathVariable("id") String classId, Model model) {
		System.out.println(classId);
		model.addAttribute("isAdmin", sessionService.currentUserIsAdmin(request));
		
		// Adicionar email do usuário logado
		String userEmail = sessionService.getCurrentUser(request);
		model.addAttribute("userEmail", userEmail);
		
		Optional<Disciplina> optDisciplina = disciplinaService.buscarPorCodigo(classId);
		if (optDisciplina.isPresent()) {
			Disciplina disciplina = optDisciplina.get();

			logger.debug("Disciplina encontrada no /class/{id}");

			List<Professor.ProfessorResumo> professors = new ArrayList<>(
				disciplina.getProfessores().stream()
					.map(Professor.ProfessorResumo::from)
					.toList()
			);

			logger.debug("Lista de professores carregada no /class/{id}");

			// ✅ Carregar avaliações (apenas ratings)
			List<AvaliacaoService.AvaliacaoDTO> avaliacoes = avaliacaoService.buscarTodasAvaliacoesDisciplina(classId);

			logger.debug("Lista de avaliações (ratings) carregada no /class/{id}");

			// ✅ Carregar comentários da disciplina (sem professor)
			List<ComentarioService.ComentarioDTO> comentariosDisciplina = 
				comentarioService.buscarComentariosDisciplina(disciplina, userEmail);

			// ✅ Carregar comentários de cada professor
			List<ComentarioService.ComentarioDTO> comentariosProfessores = disciplina.getProfessores().stream()
				.flatMap(prof -> comentarioService.buscarComentariosProfessor(disciplina, prof, userEmail).stream())
				.collect(Collectors.toList());

			// ✅ Combinar todos os comentários
			List<ComentarioService.ComentarioDTO> todosComentarios = Stream.concat(
				comentariosDisciplina.stream(),
				comentariosProfessores.stream()
			).collect(Collectors.toList());

			logger.debug("Lista de comentários carregada no /class/{id}: {} total", todosComentarios.size());

			model.addAttribute("disciplina", disciplina);
			model.addAttribute("professors", professors);
			model.addAttribute("classId", classId);
			model.addAttribute("avaliacoes", avaliacoes);
			model.addAttribute("comentarios", todosComentarios);
			return "class";
		} else {
			return "error";
		}
	}
	
}
