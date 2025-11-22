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
	private UserService userService;

	@Autowired
	private DisciplinaService disciplinaService;

	@Autowired
	@Lazy
	private AvaliacaoService avaliacaoService;
   
	
	@Transactional(readOnly = true)
	public List<MapaCurricularDTO> getMapaDoUsuario(String usuarioId) {
		Usuario usuario = userService.getUser(usuarioId);
		List<MapaCurricular> mapaCurricular = mapaCurricularRepository.findByUsuario(usuario);
		
		return mapaCurricular.stream().map(item -> {
            Disciplina disciplina = disciplinaRepository.findByCodigo(item.getDisciplina().getCodigo()).orElse(null);
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
	public MapaCurricular adicionarDisciplina(String usuarioEmail, String disciplinaCodigo, Integer semestre) {
		// Verificar se já existe

		Usuario usuario = userService.getUser(usuarioEmail);
		Optional<Disciplina> disciplinaOpt = disciplinaService.buscarPorCodigo(disciplinaCodigo);
		
		if (disciplinaOpt.isEmpty()) {
			System.out.println();
			throw new IllegalArgumentException("Código de disciplina não existe.");
		}

		Disciplina disciplina = disciplinaOpt.get();

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
		novo.setAvaliada(avaliacaoService.possuiAvaliacaoPorUsuarioDisciplina(usuarioEmail, disciplinaCodigo));
	
		return mapaCurricularRepository.save(novo);
	}

	@Transactional
	public void removerDisciplina(String userEmail, String disciplinaId) {
		Usuario usuario = userService.getUser(userEmail);
		Optional<Disciplina> disciplinaOpt = disciplinaService.buscarPorCodigo(disciplinaId);

		if (disciplinaOpt.isEmpty()) {
			throw new IllegalArgumentException("Código de disciplina não existe.");
		}

		mapaCurricularRepository.deleteByUsuarioAndDisciplina(usuario, disciplinaOpt.get());
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
