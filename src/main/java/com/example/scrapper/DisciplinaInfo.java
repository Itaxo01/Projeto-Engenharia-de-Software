package com.example.scrapper;

import java.util.Set;
import java.util.HashSet;


public class DisciplinaInfo {
        private String codigo;
        private String nome;
        private Set<ProfessorInfo> professores = new HashSet<>();
        
        public boolean isValida() {
            return codigo != null && !codigo.isEmpty() && 
                   nome != null && !nome.isEmpty();
        }
        
        // Getters e Setters
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        
        public Set<ProfessorInfo> getProfessores() { return professores; }
        public void setProfessores(Set<ProfessorInfo> professores) { this.professores = professores; }
    }