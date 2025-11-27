package com.example.DTO;

import com.example.model.Professor;

public record ProfessorDTO(String nome, String professorId) {
		public static ProfessorDTO from(Professor professor) {
			return new ProfessorDTO(professor.getNome(), professor.getProfessorId());
		}
	}
