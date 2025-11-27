package com.example.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

/**
 * Entidade JPA que representa a tabela de associação entre Professor e Disciplina.
 * Usa uma chave composta (ProfessorDisciplinaId) para mapear o relacionamento many-to-many.
 */
@Entity
@Table(name = "professor_disciplina")
public class ProfessorDisciplina {
	
	@EmbeddedId
	private ProfessorDisciplinaId id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("professorId")
	@JoinColumn(name = "professor_id")
	private Professor professor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("disciplinaId")
	@JoinColumn(name = "disciplina_id")
	private Disciplina disciplina;
	
	@Column(name = "ultimo_semestre")
	private String ultimoSemestre;

	/** Construtor padrão necessário para JPA. */
	public ProfessorDisciplina() {}
	
	/** Construtor completo com semestre */
	public ProfessorDisciplina(Professor professor, Disciplina disciplina, String ultimoSemestre) {
		this.professor = professor;
		this.disciplina = disciplina;
		this.ultimoSemestre = ultimoSemestre;
		this.id = new ProfessorDisciplinaId(professor.getProfessorId(), disciplina.getDisciplinaId());
	}
	
	// Getters and Setters
	public ProfessorDisciplinaId getId() { return id; }
	public void setId(ProfessorDisciplinaId id) { this.id = id; }
	
	public Professor getProfessor() { return professor; }
	public void setProfessor(Professor professor) { this.professor = professor; }
	
	public Disciplina getDisciplina() { return disciplina; }
	public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
	
	public String getUltimoSemestre() { return ultimoSemestre; }
	public void setUltimoSemestre(String ultimoSemestre) { this.ultimoSemestre = ultimoSemestre; }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProfessorDisciplina)) return false;
		ProfessorDisciplina that = (ProfessorDisciplina) o;
		return Objects.equals(id, that.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	/**
	 * Classe interna que representa a chave composta da tabela professor_disciplina.
	 */
	@Embeddable
	public static class ProfessorDisciplinaId implements Serializable {
		
		@Column(name = "professor_id")
		private String professorId;
		
		@Column(name = "disciplina_id")
		private Long disciplinaId;
		
		/** Construtor padrão necessário para JPA. */
		public ProfessorDisciplinaId() {}
		
		public ProfessorDisciplinaId(String professorId, Long disciplinaId) {
			this.professorId = professorId;
			this.disciplinaId = disciplinaId;
		}
		
		// Getters and Setters
		public String getProfessorId() { return professorId; }
		public void setProfessorId(String professorId) { this.professorId = professorId;}
		
		public Long getDisciplinaId() { return disciplinaId; }
		public void setDisciplinaId(Long disciplinaId) { this.disciplinaId = disciplinaId; }
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ProfessorDisciplinaId)) return false;
			ProfessorDisciplinaId that = (ProfessorDisciplinaId) o;
			return Objects.equals(professorId, that.professorId) &&
					 Objects.equals(disciplinaId, that.disciplinaId);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(professorId, disciplinaId);
		}
	}
}
