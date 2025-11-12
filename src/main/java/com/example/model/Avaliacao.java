package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa uma avaliação cadastrada no sistema. Essa tabela é atualmente usada para quase todas as queries do sistema.
 * <p>Uma avaliação contém os seguintes atributos principais:</p>
 * <ul>
 *   <li>{@link #nota} - Nota dada pelo usuário (1 a 5). Use -1 para indicar que a nota não foi definida</li>
 *   <li>{@link #professor} - ID do professor avaliado. Pode ser nulo se a avaliação for apenas da disciplina</li>
 *   <li>{@link #disciplina} - Código da disciplina avaliada</li>
 *   <li>{@link #usuario} - Email do usuário que fez a avaliação</li>
 *   <li>{@link #comentario} - Comentário principal associado à avaliação (opcional)</li>
 *   <li>{@link #createdAt} - Timestamp de quando a avaliação foi criada</li>
 * </ul>
 */
@Entity
@Table(
	name = "avaliacoes",
	indexes = {
		@Index(name="uniqueTupleDisplinaUsusuarioProfessor" ,columnList = "disciplina, usuario, professor", unique = true)
	})
public class Avaliacao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Professor professor;

	@ManyToOne
	@JoinColumn
	private Disciplina disciplina;

	@ManyToOne
	@JoinColumn
	private Usuario usuario;

	@Column(name = "nota", nullable = true)
	private Integer nota;

	@Column(name = "created_at")
	private java.time.Instant createdAt = java.time.Instant.now();

	// Comentário principal da avaliação
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "comentario_id")
	private Comentario comentario;

	/** Construtor completo com comentário. */
	public Avaliacao(Integer nota, Professor professor, Disciplina disciplina, Usuario usuario, Comentario comentario) {
		this.nota = nota;
		this.professor = professor;
		this.disciplina = disciplina;
		this.usuario = usuario;
		this.comentario = comentario;
		if (comentario != null) {
			comentario.setAvaliacao(this);
		}
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

	public Professor getProfessor() { return professor; }
	public void setProfessorId(Professor professor) { this.professor = professor; }

	public Disciplina getDisciplina() { return disciplina; }
	public void setDisciplinaId(Disciplina disciplina) { this.disciplina = disciplina; }

	public Usuario getUsuario() { return usuario; }
	public void setUserEmail(Usuario usuario) { this.usuario = usuario; }

	public Integer getNota() { return nota; }
	public void setNota(Integer nota) { this.nota = nota; }

	public java.time.Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }

	public Comentario getComentario() { return comentario; }
	public void setComentario(Comentario comentario) { 
		this.comentario = comentario;
		if (comentario != null) {
			comentario.setAvaliacao(this);
		}
	}

	// Métodos auxiliares para comentário principal
	public boolean hasComentario() {
		return comentario != null && comentario.getTexto() != null && !comentario.getTexto().trim().isEmpty();
	}

	public boolean hasComentariosAninhados() {
		return comentario != null && comentario.getFilhos() != null && !comentario.getFilhos().isEmpty();
	}

	public int getTotalComentarios() {
		if (comentario == null) return 0;
		return contarComentarios(comentario);
	}

	private int contarComentarios(Comentario comentario) {
		int total = 1; // o próprio comentário
		if (comentario.getFilhos() != null) {
			for (Comentario filho : comentario.getFilhos()) {
				total += contarComentarios(filho);
			}
		}
		return total;
	}

	public void clearComentario() {
		if (this.comentario != null) {
			this.comentario.setAvaliacao(null);
		}
		this.comentario = null;
	}

	@Override
	public String toString() {
		String comentarioTexto = null;
		if (comentario != null && comentario.getTexto() != null) {
			String texto = comentario.getTexto();
			comentarioTexto = texto.length() > 50 ? texto.substring(0, 50) + "..." : texto;
		}
		
		return "Avaliacao{" +
				"id=" + id +
				", nota=" + nota +
				", professorId='" + professor.getProfessorId() + '\'' +
				", disciplinaId='" + disciplina.getDisciplinaId() + '\'' +
				", userEmail='" + usuario.getUser_email() + '\'' +
				", comentario='" + (comentarioTexto != null ? comentarioTexto : "null") + '\'' +
				", createdAt=" + createdAt +
				", totalComentarios=" + getTotalComentarios() +
				'}';
	}
}