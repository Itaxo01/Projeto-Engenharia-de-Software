package com.example.scrapper;
import java.time.LocalDateTime;

class ScrapperStatus {
    private LocalDateTime ultimaExecucao;
    private LocalDateTime ultimoSucesso;
    private boolean executando;
    private int disciplinasCapturadas;
    private int professoresCapturados;
    private String ultimoErro;
    private String ultimoAdministrador;
    
    public LocalDateTime getUltimaExecucao() { return ultimaExecucao; }
    public void setUltimaExecucao(LocalDateTime ultimaExecucao) { this.ultimaExecucao = ultimaExecucao; }
    
    public LocalDateTime getUltimoSucesso() { return ultimoSucesso; }
    public void setUltimoSucesso(LocalDateTime ultimoSucesso) { this.ultimoSucesso = ultimoSucesso; }
    
    public boolean isExecutando() { return executando; }
    public void setExecutando(boolean executando) { this.executando = executando; }
    
    public int getDisciplinasCapturadas() { return disciplinasCapturadas; }
    public void setDisciplinasCapturadas(int disciplinasCapturadas) { this.disciplinasCapturadas = disciplinasCapturadas; }
    
    public int getProfessoresCapturados() { return professoresCapturados; }
    public void setProfessoresCapturados(int professoresCapturados) { this.professoresCapturados = professoresCapturados; }
    
    public String getUltimoErro() { return ultimoErro; }
    public void setUltimoErro(String ultimoErro) { this.ultimoErro = ultimoErro; }
    
    public String getUltimoAdministrador() { return ultimoAdministrador; }
    public void setUltimoAdministrador(String ultimoAdministrador) { this.ultimoAdministrador = ultimoAdministrador; }
}