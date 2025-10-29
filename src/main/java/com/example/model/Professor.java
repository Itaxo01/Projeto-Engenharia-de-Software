package com.example.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


/**
 * Entidade JPA que representa um professor cadastrado no sistema.
 * <ul>
 * <li>{@link #professorId} Identificador único do professor na plataforma Lattes.</li>
 * <li>{@link #nome} Nome completo do professor.</li>
 * </ul>
 */
@Entity
@Table(name = "professores")
public class Professor {
	@Id
	@Column(name = "professor_id", nullable = false, unique = true, length = 50)
	private String professorId; // Identificador único do professor na plataforma Lattes, já que o nome pode se repetir.

	@Column(nullable = false)
	private String nome;
	
	// Relacionamento Many-to-Many com Disciplina
	@ManyToMany(mappedBy = "professores", fetch = FetchType.LAZY)
	private Set<Disciplina> disciplinas = new HashSet<>();

	/** Construtor padrão necessário para JPA. */
	public Professor(){}

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Professor(String nome, String professorId) {
		this.nome = nome;
		this.professorId = professorId;
	}

	
	/** Nome do professor. */
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	/** ID Lattes do professor. */
	public String getProfessorId() {return professorId;}
	public void setProfessorId(String professorId) {this.professorId = professorId;}

	public Set<Disciplina> getDisciplinas() { return disciplinas; }
	public void setDisciplinas(Set<Disciplina> disciplinas) { this.disciplinas = disciplinas; }

	// Métodos auxiliares para gerenciamento de relacionamentos
	public void adicionarDisciplina(Disciplina disciplina) {
		if (!disciplinas.contains(disciplina)) {
			disciplinas.add(disciplina);
			disciplina.getProfessores().add(this);
		}
	}
	
	public void removerDisciplina(Disciplina disciplina) {
		disciplinas.remove(disciplina);
		disciplina.getProfessores().remove(this);
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professor)) return false;
        Professor professor = (Professor) o;
        return java.util.Objects.equals(professorId, professor.professorId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(professorId);
    }

    @Override
    public String toString() {
        return "Professor{" +
                "ID_LATTES='" + professorId + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }

	/**
	 * Record para representar um resumo(página de disciplina) do professor.
	 */
	public record ProfessorResumo(String nome, String professorId) {
		public static ProfessorResumo from(Professor professor) {
			return new ProfessorResumo(professor.getNome(), professor.getProfessorId());
		}
	}
}
