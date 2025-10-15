package com.example.scrapper;

import java.util.HashSet;
import java.util.Set;

import com.example.model.Professor;
import com.example.model.Disciplina;

public class ScrapingResult {
	private Set<Professor> professores = new HashSet<>();
	private Set<Disciplina> disciplinas = new HashSet<>();
	private String erro;

	public int getNumDisciplinasSalvas() { return disciplinas.size(); }
	public int getNumProfessoresSalvos() { return professores.size(); }
	public Set<Professor> getProfessores() { return professores; }
	public Set<Disciplina> getDisciplinas() { return disciplinas; }

	public void addProfessor(Professor professor){
		this.professores.add(professor);
	}

	public void addDisciplina(Disciplina disciplina){
		this.disciplinas.add(disciplina);
	}

	public String getErro() { return erro; }
	public void setErro(String erro) { this.erro = erro; }
	public boolean isSuccesso() { return erro == null; }
}