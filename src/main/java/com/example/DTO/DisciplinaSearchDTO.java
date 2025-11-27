package com.example.DTO;

import com.example.model.Disciplina;

public record DisciplinaSearchDTO (String codigo, String nome) {
	public static DisciplinaSearchDTO from(Disciplina u) {
		return new DisciplinaSearchDTO(u.getCodigo(), u.getNome());
	}
}