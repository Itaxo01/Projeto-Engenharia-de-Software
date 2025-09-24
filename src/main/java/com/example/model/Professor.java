package com.example.model;

import java.util.ArrayList;

/**
 * Entidade de domínio que representa um professor cadastrado no sistema.
 */
public class Professor {
	private String nome = null;
	private String ID_LATTES = null; // Identificador único do professor na plataforma Lattes, já que o nome pode se repetir.
	private Avaliacao avaliacao = new Avaliacao();

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Professor(String nome, String ID_LATTES) {
		this.nome = nome;
		this.ID_LATTES = ID_LATTES;
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public Professor(){}
	
	/** Nome do professor. */
	public String getNome() {
		return nome;
	}
	/** ID Lattes do professor. */
	public String getID_LATTES() {
		return ID_LATTES;
	}

	/** Avaliações do professor. */
	public void addAvaliacao(int nota) {
		this.avaliacao.addAvaliacao(nota);
	}
	public double getMedia() {
		return this.avaliacao.getMedia();
	}
	public int getCounter() {
		return this.avaliacao.getCounter();
	}
	public void addComentario(Comentario comentario) {
		this.avaliacao.addComentario(comentario);
	}
}
