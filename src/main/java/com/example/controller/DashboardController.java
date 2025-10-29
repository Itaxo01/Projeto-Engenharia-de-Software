package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.model.MapaCurricular;
import com.example.service.MapaCurricularService;
import com.example.service.SessionService;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {
	
	@Autowired
	private MapaCurricularService mapaCurricularService;

	@Autowired
	private SessionService sessionService;

	@GetMapping("/dashboard")
	public String dashboard(Model model, HttpServletRequest request) {
		String userEmail = sessionService.getCurrentUser(request);
		
		if (userEmail == null) {
			return "redirect:/login";
		}
		
		// Buscar mapa curricular do usuário
		List<MapaCurricularService.MapaCurricularDTO> mapa = mapaCurricularService.getMapaDoUsuario(userEmail);
		model.addAttribute("isAdmin", sessionService.currentUserIsAdmin(request));
		model.addAttribute("mapaCurricular", mapa);
		
		return "dashboard";
	}

	@GetMapping("/api/mapa/listar")
	@ResponseBody
	public ResponseEntity<?> listarMapaCurricular(HttpServletRequest request) {
		String userEmail = sessionService.getCurrentUser(request);
		if (userEmail == null) {
			return ResponseEntity.status(401).body("Não autenticado");
		}
		
		List<MapaCurricularService.MapaCurricularDTO> mapa = mapaCurricularService.getMapaDoUsuario(userEmail);
		return ResponseEntity.ok(mapa);
	}

	@PostMapping("/api/mapa/adicionar")
	@ResponseBody
	public ResponseEntity<?> adicionarDisciplina(@RequestBody Map<String, Object> payload, 
																HttpServletRequest request) {
		String userEmail = sessionService.getCurrentUser(request);
		if (userEmail == null) {
			return ResponseEntity.status(401).body("Não autenticado");
		}
		
		String disciplinaId = (String) payload.get("disciplinaId");
		Integer semestre = (Integer) payload.get("semestre");

		
		try {
			MapaCurricular item = mapaCurricularService.adicionarDisciplina(userEmail, disciplinaId, semestre);
			
			return ResponseEntity.ok(MapaCurricularDTO.from(item));
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(400).body(e.getMessage());
		}
	}

	@DeleteMapping("/api/mapa/remover/{disciplinaId}")
	@ResponseBody
	public ResponseEntity<?> removerDisciplina(@PathVariable String disciplinaId, 
															HttpServletRequest request) {
		String userEmail = sessionService.getCurrentUser(request);
		if (userEmail == null) {
			return ResponseEntity.status(401).body("Não autenticado");
		}
		
		mapaCurricularService.removerDisciplina(userEmail, disciplinaId);
		return ResponseEntity.ok().build();
	}

	public record MapaCurricularDTO (Long id, Integer semestre, Boolean avaliada) {
		public static MapaCurricularDTO from (MapaCurricular u) {
			return new MapaCurricularDTO(u.getId(), u.getSemestre(), u.getAvaliada());
		}

	}
}
