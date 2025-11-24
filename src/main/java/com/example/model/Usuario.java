package com.example.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;

/**
 * Entidade de domínio que representa um usuário cadastrado no sistema. Utilizada para o manejo dos dados
 * <ul>
 * <li> {@link #userEmail} Email do usuário (chave de login).</li
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
	private String userEmail;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String matricula;

	@Column(nullable = false)
	private String curso;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<Notificacao> notificacoes = new HashSet<>();;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<Avaliacao> avaliacoes = new HashSet<>();

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<MapaCurricular> mapaCurricular = new HashSet<>();

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<Comentario> comentarios = new HashSet<>();

	@Column(name = "is_admin")
	private boolean isAdmin = false;

	/**
	 * Construtor completo utilizado pelo serviço/repositório.
	 */
	public Usuario(String email, String password, String nome, String matricula, String curso) {
		this.userEmail = email;
		this.password = password;
		this.nome = nome;
		this.matricula = matricula;
		this.curso = curso;
	}

	/** Construtor padrão necessário para (de)serialização JSON. */
	public Usuario(){}

	/** Email (chave de login). */
	public String getUserEmail() { return userEmail;}
	public void setUserEmail(String email) { this.userEmail = email; }

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

	public Set<Avaliacao> getAvaliacoes() { return avaliacoes; }
	public void setAvaliacoes(Set<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }

	public Set<MapaCurricular> getMapaCurricular() { return mapaCurricular; }
	public void setMapaCurricular(Set<MapaCurricular> mapaCurricular) { this.mapaCurricular = mapaCurricular; }

	public Set<Comentario> getComentarios() { return comentarios; }
	public void setComentarios(Set<Comentario> comentarios) { this.comentarios = comentarios; }


	@Override
	public Notificacao generateAlert(Comentario comentario) {
		Notificacao notificacao = new Notificacao(this, comentario);
		notificacoes.add(notificacao);
		comentario.addNotificacao(notificacao);
		
		return notificacao;
	}
}
