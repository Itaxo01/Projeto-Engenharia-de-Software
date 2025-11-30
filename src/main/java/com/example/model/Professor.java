package com.example.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	
	// Relacionamento One-to-Many com ProfessorDisciplina
	@OneToMany(mappedBy = "professor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProfessorDisciplina> professorDisciplinas = new HashSet<>();

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

	/** Lista de relacionamentos professor-disciplina. */
	public Set<ProfessorDisciplina> getProfessorDisciplinas() { return professorDisciplinas; }
	public void setProfessorDisciplinas(Set<ProfessorDisciplina> professorDisciplinas) { 
		this.professorDisciplinas = professorDisciplinas; 
	}

	// Métodos auxiliares para gerenciamento de relacionamentos
	public void adicionarDisciplina(Disciplina disciplina, String ultimoSemestre) {
		ProfessorDisciplina pd = new ProfessorDisciplina(this, disciplina, ultimoSemestre);
		professorDisciplinas.add(pd);
		disciplina.getProfessorDisciplinas().add(pd);
	}
	
	public void removerDisciplina(Disciplina disciplina) {
		professorDisciplinas.removeIf(pd -> pd.getDisciplina().equals(disciplina));
		disciplina.getProfessorDisciplinas().removeIf(pd -> pd.getProfessor().equals(this));
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
	
}
