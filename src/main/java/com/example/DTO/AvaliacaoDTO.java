package com.example.DTO;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.Instant;

import com.example.model.Avaliacao;

public record AvaliacaoDTO(Long id, String disciplinaId, String professorId, Integer nota, Instant createdAt, Boolean isOwner) {
	public static AvaliacaoDTO from(Avaliacao avaliacao, String currentUserEmail) {
		Boolean isOwner = avaliacao.getUsuario() != null && avaliacao.getUsuario().getEmail().equals(currentUserEmail);
		return new AvaliacaoDTO(
					avaliacao.getId(),
					avaliacao.getDisciplina().getCodigo(),
					avaliacao.getProfessor() != null ? avaliacao.getProfessor().getProfessorId() : null,
					avaliacao.getNota(),
					avaliacao.getCreatedAt(),
					isOwner
		);
	}

	public boolean isAvaliacaoDisciplina() { return professorId == null; }
	public boolean isAvaliacaoProfessor() { return professorId != null; }
		
	public String getDataFormatada() {
		return createdAt.atZone(ZoneId.systemDefault())
							.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("pt-BR")));
	}	
}