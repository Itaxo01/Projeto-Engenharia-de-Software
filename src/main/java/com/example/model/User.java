package com.example.model;

import jakarta.persistence.*;

/**
 * Entidade de domínio que representa um usuário cadastrado no sistema. Utilizada para o manejo dos dados
 * <ul>
 * <li> {@link #email} Email do usuário (chave de login).</li
 * <li> {@link #password} Hash da senha armazenado.</li>
 * <li> {@link #nome} Nome completo do usuário extraído do PDF.</li
 * <li> {@link #matricula} Matrícula estudantil (ID).</li>
 * <li> {@link #curso} Curso do usuário.</li>
 * <li> {@link #isAdmin} Indica se o usuário é um administrador do sistema.</li>
 * </ul>
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
