package com.example.model;

import java.util.ArrayList;
import jakarta.persistence.*;

/**
 * Entidade JPA que representa uma avaliação cadastrada no sistema.
 * Representa uma nota de 1 a 5 dada por um usuário para um professor em uma disciplina específica.
 */
@Entity
@Table(name = "avaliacoes")
public class Avaliacao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "professor_id", nullable = false)
	private Professor professor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "disciplina_id", nullable = false)
	private Disciplina disciplina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_email", nullable = false)
	private User user;

	@Column(nullable = false)
	private Integer nota;

	@Column(name = "created_at")
	private java.time.Instant createdAt = java.time.Instant.now();

	// Relacionamento com comentários
	@OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private java.util.List<Comentario> comentarios = new ArrayList<>();

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Avaliacao(Integer nota, Professor professor, Disciplina disciplina, User user) {
		this.nota = nota;
		this.professor = professor;
		this.disciplina = disciplina;
		this.user = user;
	}

	/** Construtor padrão necessário para JPA. */
	public Avaliacao(){}

	// Getters and Setters
	public Long getId() { 
		return id;
	}
	
	public void setId(Long id) { 
		this.id = id;
	}

	public Professor getProfessor() { 
		return professor;
	}
	
	public void setProfessor(Professor professor) { 
		this.professor = professor; 
	}

	public Disciplina getDisciplina() { 
		return disciplina;
	}
	
	public void setDisciplina(Disciplina disciplina) { 
		this.disciplina = disciplina; 
	}

	public User getUser() { 
		return user;
	}
	
	public void setUser(User user) { 
		this.user = user; 
	}
	
	public Integer getNota() { 
		return nota;
	}
	
	public void setNota(Integer nota) { 
		this.nota = nota;
	}

	public java.time.Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.time.Instant createdAt) {
		this.createdAt = createdAt;
	}

	public java.util.List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(java.util.List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	// Helper methods
	public void addComentario(Comentario comentario) {
		this.comentarios.add(comentario);
		comentario.setAvaliacao(this);
	}

	public void removeComentario(Comentario comentario) {
		this.comentarios.remove(comentario);
		comentario.setAvaliacao(null);
	}

	@Override
	public String toString() {
		return "Avaliacao{" +
				"id=" + id +
				", nota=" + nota +
				", professor=" + (professor != null ? professor.getNome() : "null") +
				", disciplina=" + (disciplina != null ? disciplina.getNome() : "null") +
				", user=" + (user != null ? user.getEmail() : "null") +
				", createdAt=" + createdAt +
				'}';
	}
}