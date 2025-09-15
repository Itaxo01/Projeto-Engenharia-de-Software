package com.example.model;

/**
 * Entidade de domínio que representa um usuário cadastrado no sistema. Utilizada para o manejo dos dados
 */
public class User {
	private String email;
	private String password;
	private String nome;
	private String matricula;
	private String curso;

	/**
	 * Construtor completo utilizado pelo serviço/repositório.
	 */
	public User(String email, String password, String nome, String matricula, String curso) {
		this.email = email;
		this.password = password;
		this.nome = nome;
		this.matricula = matricula;
		this.curso = curso;
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public User(){}

	/** Email (chave de login). */
	public String getEmail() {
		return email;
	}

	/** Hash da senha armazenado. */
	public String getPassword() {
		// the password here is already hashed
		return password;
	}

	/** Nome completo do usuário extraído do PDF. */
	public String getNome() {
		return nome;
	}

	/** Matrícula estudantil (ID). */
	public String getMatricula() {
		return matricula;
	}

	/** Curso do usuário. */
	public String getCurso() {
		return curso;
	}
}
