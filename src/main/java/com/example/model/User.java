package com.example.model;

import jakarta.persistence.*;

/**
 * Entidade de domínio que representa um usuário cadastrado no sistema. Utilizada para o manejo dos dados
 */
@Entity
@Table(name = "users")
public class User {
	@Id
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String matricula;

	@Column(nullable = false)
	private String curso;

	@Column(name = "is_admin")
	private boolean isAdmin = false;

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
	public String getEmail() { return email;}
	public void setEmail(String email) { this.email = email; }

	/** Hash da senha armazenado. */
	// the password here is already hashed
	public String getPassword() { return password;}
	public void setPassword(String password) { this.password = password;}

	/** Nome completo do usuário extraído do PDF. */
	public String getNome() { return nome;}
	public void setNome(String nome) { this.nome = nome;}

	/** Matrícula estudantil (ID). */
	public String getMatricula() { return matricula;}
	public void setMatricula(String matricula) { this.matricula = matricula;} // não deve ser usado de fato

	/** Curso do usuário. */
	public String getCurso() { return curso;}
	public void setCurso(String curso) { this.curso = curso;}

	public boolean getAdmin() { return isAdmin; }
	public void setAdmin(boolean admin) { isAdmin = admin; }
}
