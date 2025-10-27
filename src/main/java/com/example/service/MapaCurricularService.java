package com.example.service;


import com.example.model.Disciplina;
import com.example.model.MapaCurricular;
import com.example.repository.DisciplinaRepository;
import com.example.repository.MapaCurricularRepository;
import com.example.service.MapaCurricularService.MapaCurricularDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class MapaCurricularService {
	@Autowired
	private MapaCurricularRepository mapaCurricularRepository;
    
	@Autowired
	private DisciplinaRepository disciplinaRepository;
   
	
	@Transactional(readOnly = true)
	public List<MapaCurricularDTO> getMapaDoUsuario(String usuarioId) {
		List<MapaCurricular> mapaCurricular = mapaCurricularRepository.findByUserEmail(usuarioId);
		
		return mapaCurricular.stream().map(item -> {
            Disciplina disciplina = disciplinaRepository.findByCodigo(item.getDisciplinaId()).orElse(null);
            if (disciplina == null) return null;
            
            return new MapaCurricularDTO(
                disciplina.getCodigo(),
                disciplina.getNome(),
                item.getSemestre(),
                item.getAvaliada()
            );
        }).filter(dto -> dto != null).collect(Collectors.toList());
	}

	@Transactional
	public MapaCurricular adicionarDisciplina(String userEmail, String disciplinaId, Integer semestre) {
		// Verificar se já existe
		var existente = mapaCurricularRepository.findByUserEmailAndDisciplinaId(userEmail, disciplinaId);
		if (existente.isPresent()) {
			// Atualizar semestre se já existe
			MapaCurricular item = existente.get();
			item.setSemestre(semestre);
			return mapaCurricularRepository.save(item);
		}
		
		// Criar novo
		MapaCurricular novo = new MapaCurricular(userEmail, disciplinaId, semestre);
		return mapaCurricularRepository.save(novo);
	}

	@Transactional
	public void removerDisciplina(String userEmail, String disciplinaId) {
		mapaCurricularRepository.deleteByUserEmailAndDisciplinaId(userEmail, disciplinaId);
	}

	@Transactional
	public void marcarComoAvaliada(String userEmail, String disciplinaId) {
		var item = mapaCurricularRepository.findByUserEmailAndDisciplinaId(userEmail, disciplinaId);
		if (item.isPresent()) {
			MapaCurricular mapa = item.get();
			mapa.setAvaliada(true);
			mapaCurricularRepository.save(mapa);
		}
	}

	public static class MapaCurricularDTO {
		private String codigo;
		private String nome;
		private Integer semestre;
		private Boolean avaliada;
		
		public MapaCurricularDTO() {}
		
		public MapaCurricularDTO(String codigo, String nome, Integer semestre, Boolean avaliada) {
			this.codigo = codigo;
			this.nome = nome;
			this.semestre = semestre;
			this.avaliada = avaliada;
		}
		
		// Getters e Setters
		public String getCodigo() { return codigo; }
		public void setCodigo(String codigo) { this.codigo = codigo; }
		
		public String getNome() { return nome; }
		public void setNome(String nome) { this.nome = nome; }
		
		public Integer getSemestre() { return semestre; }
		public void setSemestre(Integer semestre) { this.semestre = semestre; }
		
		public Boolean getAvaliada() { return avaliada; }
		public void setAvaliada(Boolean avaliada) { this.avaliada = avaliada; }
	}
}
