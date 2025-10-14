package com.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa uma avaliação cadastrada no sistema. Essa tabela é atualmente usada para quase todas as queries do sistema.
 * <p>Uma avaliação contém os seguintes atributos principais:</p>
 * <ul>
 *   <li>{@link #nota} - Nota dada pelo usuário (1 a 5). Use -1 para indicar que a nota não foi definida</li>
 *   <li>{@link #professorId} - ID do professor avaliado. Pode ser nulo se a avaliação for apenas da disciplina</li>
 *   <li>{@link #disciplinaCodigo} - Código da disciplina avaliada</li>
 *   <li>{@link #userEmail} - Email do usuário que fez a avaliação</li>
 *   <li>{@link #comentario} - Comentário principal associado à avaliação (opcional)</li>
 *   <li>{@link #createdAt} - Timestamp de quando a avaliação foi criada</li>
 * </ul>
 */
@Entity
@Table(name = "avaliacoes")
public class Avaliacao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "professor_id", nullable = true)
	private String professorId;

	@Column(name = "disciplina_codigo", nullable = false)
	private String disciplinaCodigo;

	@Column(name = "user_email", nullable = false)
	private String userEmail;

	@Column(name = "nota", nullable = true)
	private Integer nota;

	@Column(name = "created_at")
	private java.time.Instant createdAt = java.time.Instant.now();

	// Comentário principal da avaliação
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "comentario_id")
	private Comentario comentario;

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Avaliacao(Integer nota, String professorId, String disciplinaCodigo, String userEmail) {
		this.nota = nota;
		this.professorId = professorId;
		this.disciplinaCodigo = disciplinaCodigo;
		this.userEmail = userEmail;
	}

	/** Construtor completo com comentário. */
	public Avaliacao(Integer nota, String professorId, String disciplinaCodigo, String userEmail, Comentario comentario) {
		this.nota = nota;
		this.professorId = professorId;
		this.disciplinaCodigo = disciplinaCodigo;
		this.userEmail = userEmail;
		this.comentario = comentario;
		if (comentario != null) {
			comentario.setAvaliacao(this);
		}
	}

	/** Construtor para comentário sem nota. */
	public Avaliacao(String professorId, String disciplinaCodigo, String userEmail, Comentario comentario) {
		this.professorId = professorId;
		this.disciplinaCodigo = disciplinaCodigo;
		this.userEmail = userEmail;
		this.nota = -1; // Indica que a nota não foi definida
		this.comentario = comentario;
	}
	
	/* Construtor para a avaliação sem nota da disciplina*/
	public Avaliacao(String disciplinaCodigo, String userEmail, Comentario comentario) {
		this.disciplinaCodigo = disciplinaCodigo;
		this.userEmail = userEmail;
		this.nota = -1; // Indica que a nota não foi definida
		this.comentario = comentario;
	}

	/* Construtor para a avaliação com nota da disciplina*/
	public Avaliacao(String disciplinaCodigo, String userEmail, Integer nota, Comentario comentario) {
		this.disciplinaCodigo = disciplinaCodigo;
		this.userEmail = userEmail;
		this.nota = nota;
		this.comentario = comentario;
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

	public String getProfessorId() { return professorId; }
	public void setProfessorId(String professorId) { this.professorId = professorId; }

	public String getDisciplinaCodigo() { return disciplinaCodigo; }
	public void setDisciplinaCodigo(String disciplinaCodigo) { this.disciplinaCodigo = disciplinaCodigo; }

	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

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
				", professorId='" + professorId + '\'' +
				", disciplinaCodigo='" + disciplinaCodigo + '\'' +
				", userEmail='" + userEmail + '\'' +
				", comentario='" + (comentarioTexto != null ? comentarioTexto : "null") + '\'' +
				", createdAt=" + createdAt +
				", totalComentarios=" + getTotalComentarios() +
				'}';
	}
}