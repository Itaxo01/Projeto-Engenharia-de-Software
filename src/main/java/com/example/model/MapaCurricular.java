package com.example.model;

import jakarta.persistence.*;

/**
 * Entidade que representa a relação entre usuário e disciplina no mapa curricular.
 * Cada registro indica que uma disciplina foi adicionada ao semestre X do usuário.
 */
@Entity
@Table(
    name = "mapa_curricular",
    indexes = {
        @Index(name="uniquePairUsuarioDisciplina", columnList = "usuario, disciplina", unique = true)
    })
public class MapaCurricular {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;
    
    @Column(nullable = false)
    private Integer semestre; // 1, 2, 3... 8, 9, etc.
    
    @Column(nullable = false)
    private Boolean avaliada = false;
    
    // Construtor padrão
    public MapaCurricular() {}
    
    // Construtor completo
    public MapaCurricular(Usuario usuario, Disciplina disciplina, Integer semestre) {
        this();
        this.usuario = usuario;
        this.disciplina = disciplina;
        this.semestre = semestre;
        this.avaliada = false;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
    
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    
    public Boolean getAvaliada() { return avaliada; }
    public void setAvaliada(Boolean avaliada) { 
        this.avaliada = avaliada;
    }
}