package com.example.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/* Entidade que representa uma oferta de disciplina */
@Entity
@Table(name = "aulas")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_codigo")
    private Disciplina disciplina;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;
    
    private String periodo; // opcional
    
    @OneToMany(mappedBy = "aula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

	 @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	 private ArrayList<Comentario> comentarios = new ArrayList<>();
	 
	 public Aula() {}

	 public Aula(Disciplina disciplina, Professor professor, String periodo) {
		  this.disciplina = disciplina;
		  this.professor = professor;
		  this.periodo = periodo;
	 }

	 public Long getId() { return id;}

	 public Disciplina getDisciplina() { return disciplina;}

	 public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }

	 public Professor getProfessor() { return professor; }

	 public void setProfessor(Professor professor) { this.professor = professor; }

	 public String getPeriodo() { return periodo; }

	 public void setPeriodo(String periodo) { this.periodo = periodo; }

	 public List<Avaliacao> getAvaliacoes() { return avaliacoes; }

	 public void addAvaliacao(Avaliacao avaliacao) {
		  avaliacoes.add(avaliacao);
		  avaliacao.setAula(this);
	 }

	 public void addComentario(Comentario comentario) {
		this.comentarios.add(comentario);
	}
	public ArrayList<Comentario> getComentarios() {
		return comentarios;
	}
}