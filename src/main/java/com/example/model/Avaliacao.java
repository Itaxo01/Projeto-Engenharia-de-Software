package com.example.model;

import java.util.ArrayList;

/**
 * Entidade de domínio que representa uma avaliação cadastrada no sistema.
 * Simplesmente representa uma nota de 1 a 5, sem comentários.
 */
public class Avaliacao {
	private int nota = 0;
	private double media = 0.0;
	private int counter = 0;
	private ArrayList<Comentario> comentarios = new ArrayList<>();


	/** Construtor completo utilizado pelo serviço/repositório. */
	public Avaliacao(int nota) {
		this.nota = nota;
		this.counter = 1;
		this.media = nota;
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public Avaliacao(){}

	/** Média das avaliações. */
	public double getMedia() {
		return media;
	}
	/** Contador de avaliações. */
	public int getCounter() {
		return counter;
	}
	public void addAvaliacao(int nota) {
		this.media = (this.media * this.counter + nota) / (this.counter + 1);
		this.nota += nota;
		this.counter++;
	}
	public void addComentario(Comentario comentario) {
		this.comentarios.add(comentario);
	}
	public ArrayList<Comentario> getComentarios() {
		return comentarios;
	}
}