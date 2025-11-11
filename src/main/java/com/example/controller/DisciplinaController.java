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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.Avaliacao;
import com.example.service.AvaliacaoService;
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

	// @Autowired
	// private ProfessorService professorService;
	
	// Aqui o id utilizado é o mesmo que o codigo utilizado no banco de dados
	@GetMapping("/class/{id}")
	public String classDetails(HttpServletRequest request, @PathVariable("id") String classId, Model model) {
		System.out.println(classId);
		model.addAttribute("isAdmin", sessionService.currentUserIsAdmin(request));
		Optional<Disciplina> optDisciplina = disciplinaService.buscarPorCodigo(classId);
		if (optDisciplina.isPresent()) {
			Disciplina disciplina = optDisciplina.get();

			List<Professor.ProfessorResumo> professors = new ArrayList<Professor.ProfessorResumo>(disciplina.getProfessores().stream().map(Professor.ProfessorResumo::from).toList());

			List<AvaliacaoService.AvaliacaoDTO> avaliacoes = avaliacaoService.buscarTodasAvaliacoesDisciplina(classId);
			
			model.addAttribute("disciplina", disciplina);
			model.addAttribute("professors", professors);
			model.addAttribute("classId", classId);
			model.addAttribute("avaliacoes", avaliacoes);
			return "class";
		} else {
			return "error";
		}
	}


	// A avaliação terá ou comentário ou nota, em primeira instância, visto que os dois serão criados separadamente
	// Ao adicionar uma nota a uma avaliação que já existe, só se modifica a nota
	// mesma coisa para o comentário

	@PostMapping("/class/addNota")
	@ResponseBody
	public ResponseEntity<?> addNota(@RequestBody Map<String, Object> payload, HttpServletRequest request) {

		String usuarioEmail = sessionService.getCurrentUser(request);
		String disciplinaId = (String) payload.get("disciplinaId");
		String professorId = (String) payload.get("professorId");
		Integer nota = (Integer) payload.get("nota");

		try {
			avaliacaoService.addNota(professorId, disciplinaId, usuarioEmail, nota);
			return ResponseEntity.ok("Avaliação adicionada/atualizada com sucesso.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}
	}

	@PostMapping("/class/addComentario")
	@ResponseBody
	public ResponseEntity<?> addComentario(@RequestBody Map<String, Object> payload, HttpServletRequest request) {

		String usuarioEmail = sessionService.getCurrentUser(request);
		String disciplinaId = (String) payload.get("disciplinaId");
		String professorId = (String) payload.get("professorId");
		Integer nota = (Integer) payload.get("nota");

		// Esse mapping precisa lidar com a chatisse de criar o comentário e depois a avaliação
		
		return null;
	}

	@PostMapping("/class/addComentarioFilho")
	@ResponseBody
	public ResponseEntity<?> addComentarioFilho(@RequestBody Map<String, Object> payload, HttpServletRequest request) {

		String userEmail = sessionService.getCurrentUser(request);
		Long comentarioPaiId = (Long) payload.get("comentarioPaiId");
		Integer nota = (Integer) payload.get("nota");

		// Esse mapping só cria o comentário filho de outro comentário. Não há relação direta dele com a avaliação
		
		return null;
	}


}
