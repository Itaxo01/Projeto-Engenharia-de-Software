package com.example.scrapper;

import java.util.HashSet;
import java.util.Set;

import com.example.model.Professor;

public class ScrapingResult {
	private int disciplinasSalvas = 0;
	private Set<Professor> professores = new HashSet<>();
	private String erro;

	public void addDisciplinasSalvas(int disciplinasSalvas) { this.disciplinasSalvas += disciplinasSalvas; }

	public int getDisciplinasSalvas() { return disciplinasSalvas; }
	public int getProfessoresSalvos() { return professores.size(); }

	public void addProfessores(Set<Professor> professores){
		this.professores.addAll(professores);
	}

	public String getErro() { return erro; }
	public void setErro(String erro) { this.erro = erro; }
	public boolean isSuccesso() { return erro == null; }
}