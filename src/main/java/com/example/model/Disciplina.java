package com.example.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Entidade JPA que representa uma disciplina cadastrada no sistema.
 */
@Entity
@Table(name = "disciplinas")
public class Disciplina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    // Relacionamento Many-to-Many com Professor
    @ManyToMany(mappedBy = "disciplinas", fetch = FetchType.LAZY)
    private List<Professor> professores = new ArrayList<>();
    
    // Relacionamento com avaliações
    @OneToMany(mappedBy = "disciplina", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes = new ArrayList<>();
    
    /**
     * Construtor completo utilizado pelo serviço/repositório.
     */
    public Disciplina(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }
    
    public Disciplina(String codigo, String nome, String descricao) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
    }

    /** Construtor padrão necessário para JPA. */
    public Disciplina(){}

    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    /** Código da disciplina. */
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /** Nome da disciplina. */
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public List<Professor> getProfessores() {
        return professores;
    }
    
    public void setProfessores(List<Professor> professores) {
        this.professores = professores;
    }
    
    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }
    
    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
    
    // Helper methods
    public void addProfessor(Professor professor) {
        this.professores.add(professor);
        professor.getDisciplinas().add(this);
    }
    
    public void removeProfessor(Professor professor) {
        this.professores.remove(professor);
        professor.getDisciplinas().remove(this);
    }
    
    @Override
    public String toString() {
        return "Disciplina{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
