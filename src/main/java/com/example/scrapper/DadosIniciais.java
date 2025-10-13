package com.example.scrapper;

import java.util.HashMap;

public class DadosIniciais {
	private String semestreAtual;
	private HashMap<String, String> centros = new HashMap<>();
	
	public String getSemestreAtual() { return semestreAtual; }
	public void setSemestreAtual(String semestreAtual) { this.semestreAtual = semestreAtual; }
	
	public HashMap<String, String> getCentros() { return centros; }
	public void setCentros(HashMap<String, String> centros) { this.centros = centros; }
}