package com.example.factory;

import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.model.Notificacao;
import com.example.model.Professor;
import com.example.model.Usuario;

public class ComentarioFactory {
	public static Comentario createComentario(Usuario usuario, String texto, Disciplina disciplina, Professor professor) {
		return new Comentario(usuario, texto, disciplina, professor);
	}

	public static Comentario createReply(Usuario usuario, String texto, Comentario comentarioPai) {
		Comentario resposta = new Comentario(usuario, texto, comentarioPai);

		if(comentarioPai.getUsuario().getUserEmail().equals(usuario.getUserEmail())) {
			// Não notificar se o usuário respondeu a si mesmo
			return resposta;
		}
		
		Notificacao notificacao = comentarioPai.getUsuario().generateAlert(resposta);
		resposta.addNotificacao(notificacao);

		return resposta;
	}
}
