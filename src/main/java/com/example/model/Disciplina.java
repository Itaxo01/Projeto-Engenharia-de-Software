package com.example.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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


	 @ElementCollection(fetch = FetchType.EAGER)
	 @CollectionTable(
		  name = "professor_disciplina",
		  joinColumns = @JoinColumn(name = "disciplina_id")
	 )
	 @Column(name = "professor_id")
	 private Set<String> professores = new HashSet<>();
    
   //  // Relacionamento One-to-Many com Avaliacao
    // @OneToMany(mappedBy = "disciplina_id", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    // private ArrayList<Avaliacao> avaliacoes = new ArrayList<>();
    
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
    
    /** Lista de professores que lecionam esta disciplina. */
    public Set<String> getProfessores() { 
		if(professores == null) {
			professores = new HashSet<>();
		}
		return professores; 
	}
    public void setProfessores(Set<String> professores) { 
		this.professores = professores != null ? professores : new HashSet<>();
	 }

   //  /** Lista de avaliações desta disciplina. */
    // public ArrayList<Avaliacao> getAvaliacoes() { 
	// 	if(avaliacoes == null) {
	// 		avaliacoes = new ArrayList<>();
	// 	}
	// 	return avaliacoes;
	// }
    // public void setAvaliacoes(ArrayList<Avaliacao> avaliacoes) { 
	// 	this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
	// }

    // Métodos auxiliares para gerenciamento de relacionamentos
	 public void adicionarProfessor(Professor professor) {adicionarProfessor(professor.getProfessorId());}
    public void adicionarProfessor(String professor) {
      if(professores == null) {
	 	  professores = new HashSet<>();
		}

		professores.add(professor);
    }

	 public boolean temProfessor(Professor professor) { return temProfessor(professor.getProfessorId()); }
	 public boolean temProfessor(String professor) {
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
