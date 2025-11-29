package com.example.DTO;

import com.example.model.Professor;
import java.util.HashSet;
import java.util.Set;

public record ProfessorDTO(String nome, String professorId, String semestre) {
		public static ProfessorDTO from(Professor professor, String semestre) {
			return new ProfessorDTO(professor.getNome(), professor.getProfessorId(), semestre);
		}

		public static Set<ProfessorDTO> fromSet(Set<Professor> professors) {
			Set<ProfessorDTO> dtoSet = new HashSet<>();
			for (Professor professor : professors) {
				dtoSet.add(new ProfessorDTO(professor.getNome(), professor.getProfessorId(), null));
			}
			return dtoSet;
		}
	}
