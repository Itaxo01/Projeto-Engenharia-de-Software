package com.example.service;


import com.example.model.Disciplina;
import com.example.model.MapaCurricular;
import com.example.model.Usuario;
import com.example.repository.DisciplinaRepository;
import com.example.repository.MapaCurricularRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class MapaCurricularService {
	@Autowired
	private MapaCurricularRepository mapaCurricularRepository;
    
	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@Autowired
	private DisciplinaService disciplinaService;

	@Autowired
	private AvaliacaoService avaliacaoService;
	
	@Transactional(readOnly = true)
	public List<MapaCurricularDTO> getMapaDoUsuario(Usuario usuario) {
		List<MapaCurricular> mapaCurricular = mapaCurricularRepository.findByUsuario(usuario);
		
		return mapaCurricular.stream().map(item -> {
            Disciplina disciplina = disciplinaRepository.findByCodigo(item.getDisciplina().getCodigo()).orElse(null);
            if (disciplina == null) return null;
            
            return new MapaCurricularDTO(
                disciplina.getCodigo(),
                disciplina.getNome(),
                item.getSemestre(),
                avaliacaoService.possuiAvaliacaoPorUsuarioDisciplina(usuario, disciplina)
            );
        }).filter(dto -> dto != null).collect(Collectors.toList());
	}

	@Transactional
	public MapaCurricular adicionarDisciplina(Usuario usuario, Disciplina disciplina, Integer semestre) {
		// Verificar se já existe

		if (disciplina == null) {
			throw new IllegalArgumentException("Disciplina não existe.");
		}
		if(usuario == null) {
			throw new IllegalArgumentException("Usuário não existe.");
		}

		var existente = mapaCurricularRepository.findByUsuarioAndDisciplina(usuario, disciplina);
		if (existente.isPresent()) {
			// Atualizar semestre se já existe
			MapaCurricular item = existente.get();
			item.setSemestre(semestre);
			return mapaCurricularRepository.save(item);
		}
		
		// Criar novo
		MapaCurricular novo = new MapaCurricular(usuario, disciplina, semestre);

		// verifica se ha alguma avaliacao de usuario para disciplina		
		novo.setAvaliada(avaliacaoService.possuiAvaliacaoPorUsuarioDisciplina(usuario, disciplina));
	
		return mapaCurricularRepository.save(novo);
	}

	@Transactional
	public void removerDisciplina(Usuario usuario, Disciplina disciplina) {
		if (disciplina == null) {
			throw new IllegalArgumentException("Disciplina não existe.");
		}
		if(usuario == null) {
			throw new IllegalArgumentException("Usuário não existe.");
		}

		mapaCurricularRepository.deleteByUsuarioAndDisciplina(usuario, disciplina);
	}

	@Transactional
	public void marcarComoAvaliada(Usuario usuario, Disciplina disciplina) {
		var item = mapaCurricularRepository.findByUsuarioAndDisciplina(usuario, disciplina);
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
