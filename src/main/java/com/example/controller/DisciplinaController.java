package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.model.Disciplina;
import com.example.model.Professor;
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
			
			model.addAttribute("disciplina", disciplina);
			model.addAttribute("professors", professors);
			model.addAttribute("classId", classId);
			return "class";
		} else {
			return "error";
		}
	}

	@GetMapping("/disciplina/{id}")
	public String disciplinaDetails(HttpServletRequest request, @PathVariable("id") String classId, Model model) {
		return classDetails(request, classId, model);
	}
}
