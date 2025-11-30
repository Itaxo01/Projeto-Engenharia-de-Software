package com.example.factory;

import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.Usuario;

public class ComentarioFactory {
	public static Comentario criarComentario(Usuario usuario, String texto, Disciplina disciplina, Professor professor, Comentario comentarioPai) {
		if (comentarioPai == null) {
			return new Comentario(usuario, texto, disciplina, professor);
		} else {
			return new Comentario(usuario, texto, comentarioPai);
		}
	}
	
	public static Comentario createReply(Usuario usuario, String texto, Comentario comentarioPai) {
		Comentario resposta = new Comentario(usuario, texto, comentarioPai);

		if(comentarioPai.getUsuario().getEmail().equals(usuario.getEmail())) {
			// Não notificar se o usuário respondeu a si mesmo
			return resposta;
		}
		
		return resposta;
	}
}
