package com.example.model;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;

/**
 * Entidade JPA que representa um professor cadastrado no sistema.
 */
@Entity
@Table(name = "professores")
public class Professor {
	@Id
	private String ID_LATTES; // Identificador único do professor na plataforma Lattes, já que o nome pode se repetir.

	@Column(nullable = false)
	private String nome;
	
	// Relacionamento Many-to-Many com Disciplina
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinTable(
		name = "professor_disciplina",
		joinColumns = @JoinColumn(name = "professor_id"),
		inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private List<Disciplina> disciplinas = new ArrayList<>();
	
	// Relacionamento com avaliações
	@OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Avaliacao> avaliacoes = new ArrayList<>();
	
	/** Construtor completo utilizado pelo serviço/repositório. */
	public Professor(String nome, String ID_LATTES) {
		this.nome = nome;
		this.ID_LATTES = ID_LATTES;
	}

	/** Construtor padrão necessário para JPA. */
	public Professor(){}
	
	/** Nome do professor. */
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	/** ID Lattes do professor. */
	public String getID_LATTES() {return ID_LATTES;}
	public void setID_LATTES(String iD_LATTES) {ID_LATTES = iD_LATTES;}
	
	public List<Disciplina> getDisciplinas() {
		return disciplinas;
	}
	
	public void setDisciplinas(List<Disciplina> disciplinas) {
		this.disciplinas = disciplinas;
	}
	
	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}
	
	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}
	
	// Helper methods
	public void addDisciplina(Disciplina disciplina) {
		this.disciplinas.add(disciplina);
		disciplina.getProfessores().add(this);
	}
	
	public void removeDisciplina(Disciplina disciplina) {
		this.disciplinas.remove(disciplina);
		disciplina.getProfessores().remove(this);
	}
}
