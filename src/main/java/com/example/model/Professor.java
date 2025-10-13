package com.example.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import jakarta.persistence.*;


/**
 * Entidade JPA que representa um professor cadastrado no sistema.
 * <ul>
 * <li>{@link #nome} Nome completo do professor.</li>
 * <li>{@link #ID_LATTES} Identificador único do professor na plataforma Lattes.</li>
 * </ul>
 */
@Entity
@Table(name = "professores")
public class Professor {
	@Id
	@Column(name = "id_lattes", nullable = false, unique = true, length = 50)
	private String ID_LATTES; // Identificador único do professor na plataforma Lattes, já que o nome pode se repetir.

	@Column(nullable = false)
	private String nome;
	
	// Relacionamento Many-to-Many com Disciplina
	@ManyToMany(mappedBy = "professores", fetch = FetchType.LAZY)
	private Set<Disciplina> disciplinas = new HashSet<>();

	// Relacionamento One-to-Many com Avaliacao  
	@OneToMany(mappedBy = "professorId", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Avaliacao> avaliacoes = new ArrayList<>();
	
	/** Construtor padrão necessário para JPA. */
	public Professor(){}

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Professor(String nome, String ID_LATTES) {
		this.nome = nome;
		this.ID_LATTES = ID_LATTES;
	}

	
	/** Nome do professor. */
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	/** ID Lattes do professor. */
	public String getID_LATTES() {return ID_LATTES;}
	public void setID_LATTES(String ID_LATTES) {this.ID_LATTES = ID_LATTES;}

	public Set<Disciplina> getDisciplinas() { return disciplinas; }
	public void setDisciplinas(Set<Disciplina> disciplinas) { this.disciplinas = disciplinas; }

	/** Lista de avaliações do professor. */
	public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
	public void setAvaliacoes(List<Avaliacao> avaliacoes) { 
		this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
	}

	public void addAvaliacao(Avaliacao avaliacao){
		avaliacoes.add(avaliacao);
		avaliacao.setProfessorId(this.ID_LATTES);
	}

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
        return java.util.Objects.equals(ID_LATTES, professor.ID_LATTES);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(ID_LATTES);
    }

    @Override
    public String toString() {
        return "Professor{" +
                "ID_LATTES='" + ID_LATTES + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
