package com.example.model;

import java.util.ArrayList;

/**
 * Entidade de domínio que representa uma disciplina cadastrada no sistema.
 */
public class Subject {
	private String codigo;
	private String nome;
	private Avaliacao avaliacao = new Avaliacao();
	private ArrayList<Professor> professores = new ArrayList<>();

	/**
	 * Construtor completo utilizado pelo serviço/repositório.
	 */
	public Subject(String codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public Subject(){}

	/** Código da disciplina. */
	public String getCodigo() {
		return codigo;
	}

	/** Nome da disciplina. */
	public String getNome() {
		return nome;
	}

	/** Avaliações da disciplina. */
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
