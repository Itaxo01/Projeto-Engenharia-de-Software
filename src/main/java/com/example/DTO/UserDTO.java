package com.example.DTO;

import com.example.model.Usuario;

public record UserDTO(String email, String nome, String matricula, String curso, boolean admin){
		/** Constrói o DTO a partir da entidade {@link com.example.model.Usuario}. */
		public static UserDTO from(Usuario u){
			return new UserDTO(u.getEmail(), u.getNome(), u.getMatricula(), u.getCurso(), u.getIsAdmin());
		}
	}