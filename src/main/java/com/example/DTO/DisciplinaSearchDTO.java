package com.example.DTO;

import com.example.model.Disciplina;
import java.util.Set;

public record DisciplinaSearchDTO (String codigo, String nome, Set<ProfessorDTO> professores) {
	public static DisciplinaSearchDTO from(Disciplina u) {
		return new DisciplinaSearchDTO(u.getCodigo(), u.getNome(), ProfessorDTO.fromSet(u.getProfessores()));
	}
}