package com.example.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa uma disciplina cadastrada no sistema.
 * <ul>
 * <li> {@link #codigo} Código único da disciplina (ex: "INE5101").</li>
 * <li> {@link #nome} Nome da disciplina (ex: "Introdução à Ciência da Computação").</li>
 * </ul>
 */
@Entity
@Table(name = "disciplinas")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disciplinaId;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 300)
    private String nome;
	 
	 @OneToMany(mappedBy = "disciplina", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<MapaCurricular> mapaCurricular = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name="professor_disciplina",
        joinColumns=
            @JoinColumn(name="disciplina_id", referencedColumnName="disciplinaId"),
        inverseJoinColumns=
            @JoinColumn(name="professor_id", referencedColumnName="professor_id")
    )
    private Set<Professor> professores = new HashSet<>();

    /**
     * Construtor completo utilizado pelo serviço/repositório.
     */
    public Disciplina(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

	 /** Construtor padrão necessário para JPA. */
    public Disciplina(){}

    // Getters and Setters
    public Long getDisciplinaId() { return disciplinaId; }
    public void setDisciplinaId(Long id) { this.disciplinaId = id; }

    /** Código da disciplina. */
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    /** Nome da disciplina. */
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
	 public Set<MapaCurricular> getMapaCurricular() { return mapaCurricular; }
	 public void setMapaCurricular(Set<MapaCurricular> mapaCurriculars) { this.mapaCurricular = mapaCurriculars; }

    /** Lista de professores que lecionam esta disciplina. */
    public Set<Professor> getProfessores() { 
		if(professores == null) {
			professores = new HashSet<>();
		}
		return professores; 
	}
    public void setProfessores(Set<Professor> professores) { 
		this.professores = professores != null ? professores : new HashSet<>();
	 }

    // Métodos auxiliares para gerenciamento de relacionamentos
    // public void adicionarProfessor(Professor professor) {adicionarProfessor(professor.getProfessorId());}
    public void adicionarProfessor(Professor professor) {
      if(professores == null) {
	 	  professores = new HashSet<>();
		}

		professores.add(professor);
    }

	//  public boolean temProfessor(Professor professor) { return temProfessor(professor.getProfessorId()); }
	 public boolean temProfessor(Professor professor) {
		if(professores == null) {
	 	  professores = new HashSet<>();
		}
		return professores.contains(professor);
	}


    
	 @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disciplina)) return false;
        Disciplina disciplina = (Disciplina) o;
        return java.util.Objects.equals(codigo, disciplina.codigo);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Disciplina{" +
                "id=" + disciplinaId +
                ", codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
