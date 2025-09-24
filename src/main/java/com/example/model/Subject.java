package com.example.model;

/**
 * Entidade de domínio que representa uma disciplina cadastrada no sistema.
 */
public class Subject {
	private String codigo;
	private String nome;

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
}
