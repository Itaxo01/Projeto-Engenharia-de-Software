package com.example.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entidade que representa a relação entre usuário e disciplina no mapa curricular.
 * Cada registro indica que uma disciplina foi adicionada ao semestre X do usuário.
 */
@Entity
@Table(name = "mapa_curricular",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_email", "disciplina_id"}))
public class MapaCurricular {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @Column(name = "disciplina_id", nullable = false)
    private String disciplinaId;
    
    @Column(nullable = false)
    private Integer semestre; // 1, 2, 3... 8, 9, etc.
    
    @Column(nullable = false)
    private Boolean avaliada = false;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Construtor padrão
    public MapaCurricular() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    // Construtor completo
    public MapaCurricular(String userEmail, String disciplinaId, Integer semestre) {
        this();
        this.userEmail = userEmail;
        this.disciplinaId = disciplinaId;
        this.semestre = semestre;
        this.avaliada = false;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getDisciplinaId() { return disciplinaId; }
    public void setDisciplinaId(String disciplinaId) { this.disciplinaId = disciplinaId; }
    
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    
    public Boolean getAvaliada() { return avaliada; }
    public void setAvaliada(Boolean avaliada) { 
        this.avaliada = avaliada;
        this.updatedAt = Instant.now();
    }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}