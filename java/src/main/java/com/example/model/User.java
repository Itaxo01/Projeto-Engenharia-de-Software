package com.example.model;

public class User {
	private String email;
	private String password;
	private String nome;
	private String matricula;
	private String curso;

	public User(String email, String password, String nome, String matricula, String curso) {
		this.email = email;
		this.password = password;
		this.nome = nome;
		this.matricula = matricula;
		this.curso = curso;
	}
	public User(){}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		// the password here is already hashed
		return password;
	}

	public String getNome() {
		return nome;
	}

	public String getMatricula() {
		return matricula;
	}

	public String getCurso() {
		return curso;
	}
}
