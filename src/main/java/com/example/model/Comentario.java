package com.example.model;

import java.util.ArrayList;

/**
 * Entidade de domínio que representa um comentário feito por um usuário em uma avaliação.
 */
public class Comentario {
	private User usuario;
	private String texto;
	private String data; // Data e hora do comentário no formato ISO 8601.
	private ArrayList<Comentario> respostas;

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Comentario(User usuario, String texto, String data) {
		this.usuario = usuario;
		this.texto = texto;
		this.data = data;
		this.respostas = new ArrayList<>();
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public Comentario(){}

	/** Usuário que fez o comentário. */
	public User getUsuario() {
		return usuario;
	}

	/** Texto do comentário. */
	public String getTexto() {
		return texto;
	}

	/** Data do comentário. */
	public String getData() {
		return data;
	}
}
