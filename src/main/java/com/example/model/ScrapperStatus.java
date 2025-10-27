package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scrapper_status")
public class ScrapperStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ultima_execucao")
    private LocalDateTime ultimaExecucao;

    @Column(name = "ultimo_sucesso")
    private LocalDateTime ultimoSucesso;

    @Column(name = "executando")
    private boolean executando = false;

    @Column(name = "disciplinas_capturadas")
    private int disciplinasCapturadas = 0;

    @Column(name = "professores_capturados")
    private int professoresCapturados = 0;

    @Column(name = "ultimo_erro", length = 1000)
    private String ultimoErro;

    @Column(name = "ultimo_administrador", length = 150)
    private String ultimoAdministrador;

    @Column(name = "total_execucoes")
    private int totalExecucoes = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime creattedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ScrapperStatus() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getUltimaExecucao() { return ultimaExecucao; }
    public void setUltimaExecucao(LocalDateTime ultimaExecucao) {
        this.ultimaExecucao = ultimaExecucao;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getUltimoSucesso() { return ultimoSucesso; }
    public void setUltimoSucesso(LocalDateTime ultimoSucesso) {
        this.ultimoSucesso = ultimoSucesso;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isExecutando() { return executando; }
    public void setExecutando(boolean executando) {
        this.executando = executando;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getDisciplinasCapturadas() { return disciplinasCapturadas; }
    public void setDisciplinasCapturadas(int disciplinasCapturadas) {
        this.disciplinasCapturadas = disciplinasCapturadas;
        this.updatedAt = LocalDateTime.now();
    }

    public int getProfessoresCapturados() { return professoresCapturados; }
    public void setProfessoresCapturados(int professoresCapturados) {
        this.professoresCapturados = professoresCapturados;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUltimoErro() { return ultimoErro; }
    public void setUltimoErro(String ultimoErro) {
        this.ultimoErro = ultimoErro;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getUltimoAdministrador() { return ultimoAdministrador; }
    public void setUltimoAdministrador(String ultimoAdministrador) {
        this.ultimoAdministrador = ultimoAdministrador;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreattedAt() { return creattedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void incrementarTotalExecucoes() {
        this.totalExecucoes++;
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarInicioExecucao(String administrador) {
        this.executando = true;
        this.ultimaExecucao = LocalDateTime.now();
        this.ultimoAdministrador = administrador;
        this.ultimoErro = null;
        this.incrementarTotalExecucoes();
    }

    public void marcarFimExecucao(boolean sucesso, int disciplinasCapturadas, int professoresCapturados, String erro) {
        this.executando = false;
        if (sucesso) {
            this.ultimoErro = null;
            this.ultimoSucesso = LocalDateTime.now();
            this.disciplinasCapturadas = disciplinasCapturadas;
            this.professoresCapturados = professoresCapturados;
        } else {
            if(erro != null) this.ultimoErro = erro;
            else this.ultimoErro = "Erro desconhecido";
        }
        this.updatedAt = LocalDateTime.now();
    }
}