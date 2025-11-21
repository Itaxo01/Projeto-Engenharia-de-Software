package com.example.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

/**
 * Entidade de domínio que representa um usuário cadastrado no sistema. Utilizada para o manejo dos dados
 * <ul>
 * <li> {@link #user_email} Email do usuário (chave de login).</li
 * <li> {@link #password} Hash da senha armazenado.</li>
 * <li> {@link #nome} Nome completo do usuário extraído do PDF.</li
 * <li> {@link #matricula} Matrícula estudantil (ID).</li>
 * <li> {@link #curso} Curso do usuário.</li>
 * <li> {@link #isAdmin} Indica se o usuário é um administrador do sistema.</li>
 * </ul>
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements ComentarioObserver{
	@Id
	private String user_email;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String matricula;

	@Column(nullable = false)
	private String curso;

	@OneToMany(mappedBy = "usuario")
	private Set<Notificacao> notificacoes = new HashSet<>();;

	@Column(name = "is_admin")
	private boolean isAdmin = false;

	/**
	 * Construtor completo utilizado pelo serviço/repositório.
	 */
	public Usuario(String email, String password, String nome, String matricula, String curso) {
		this.user_email = email;
		this.password = password;
		this.nome = nome;
		this.matricula = matricula;
		this.curso = curso;
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public Usuario(){}

	/** Email (chave de login). */
	public String getUser_email() { return user_email;}
	public void setUser_email(String email) { this.user_email = email; }

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

	public Set<Notificacao> getNotificacoes() {
		return notificacoes;
	}
	public void setNotificacoes(Set<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}

	@Override
	public Notificacao generateAlert(Comentario comentario) {
		Notificacao notificacao = new Notificacao(this, comentario);
		notificacoes.add(notificacao);
		return notificacao;
	}
}
