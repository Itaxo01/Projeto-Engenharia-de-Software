package com.example.DTO;

import com.example.model.Usuario;

public record UserDto(String email, String nome, String matricula, String curso, boolean admin){
		/** Constrói o DTO a partir da entidade {@link com.example.model.Usuario}. */
		public static UserDto from(Usuario u){
			return new UserDto(u.getUserEmail(), u.getNome(), u.getMatricula(), u.getCurso(), u.getAdmin());
		}
	}