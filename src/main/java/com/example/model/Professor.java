package com.example.model;

/**
 * Entidade de domínio que representa um professor cadastrado no sistema.
 */
public class Professor {
	private String nome;
	private String ID_LATTES; // Identificador único do professor na plataforma Lattes, já que o nome pode se repetir.

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
}
