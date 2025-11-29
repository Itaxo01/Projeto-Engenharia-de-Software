package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.ProfessorDisciplina;
import com.example.service.AvaliacaoService;
import com.example.service.ComentarioService;
import com.example.service.DisciplinaService;
import com.example.service.SessionService;

import com.example.DTO.ComentarioDTO;
import com.example.DTO.AvaliacaoDTO;
import com.example.DTO.ProfessorDTO;

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
		logger.debug("Acessando detalhes da disciplina: " + classId);
		
		
		// Adicionar email do usuário logado
		String userEmail = sessionService.getCurrentUser(request);
		
		Optional<Disciplina> optDisciplina = disciplinaService.buscarPorCodigo(classId);
		if (optDisciplina.isPresent()) {
			Disciplina disciplina = optDisciplina.get();

			logger.debug("Disciplina encontrada no /class/{id}");

			// ✅ Construir lista de professores a partir de ProfessorDisciplina para incluir ultimoSemestre
			List<ProfessorDTO> professors = new ArrayList<>(
				disciplina.getProfessorDisciplinas().stream()
					.map(pd -> ProfessorDTO.from(pd.getProfessor(), pd.getUltimoSemestre()))
					.toList()
			);

			logger.debug("Lista de professores carregada no /class/{id}");

			// ✅ Carregar avaliações (apenas ratings)
			List<AvaliacaoDTO> avaliacoes = avaliacaoService.buscarTodasAvaliacoesDisciplina(classId, sessionService.getCurrentUser(request));

			logger.debug("Lista de avaliações (ratings) carregada no /class/{id}");

			// ✅ Carregar apenas comentários de professores (comentários de disciplina não existem mais)
			List<ComentarioDTO> todosComentarios = disciplina.getProfessores().stream()
				.flatMap(prof -> comentarioService.buscarComentariosProfessor(disciplina, prof, userEmail).stream())
				.collect(Collectors.toList());

			logger.debug("Lista de comentários carregada no /class/{id}: {} total", todosComentarios.size());

			// ✅ Flag para indicar se há professores na disciplina
			boolean hasProfessors = !professors.isEmpty();

			model.addAttribute("disciplina", disciplina);
			model.addAttribute("professors", professors);
			model.addAttribute("classId", classId);
			model.addAttribute("avaliacoes", avaliacoes);
			model.addAttribute("comentarios", todosComentarios);
			model.addAttribute("hasProfessors", hasProfessors);
			return "class";
		} else {
			return "error";
		}
	}
	
}
