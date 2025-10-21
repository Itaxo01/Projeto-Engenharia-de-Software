package com.example.controller;

import com.example.model.Disciplina;
import com.example.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/* Só vai fazer uma coisa, retornar as disciplinas ao front-end ao selecionar a barra de pesquisa */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    @Autowired
    private DisciplinaService disciplinaService;
    
    @GetMapping("/disciplinas")
    public ResponseEntity<List<DisciplinaSearchDTO>> searchDisciplinas() {
        List<Disciplina> resultados = disciplinaService.buscarTodas();
		  List<DisciplinaSearchDTO> dtoList = resultados.stream()
				.map(d -> new DisciplinaSearchDTO(d.getCodigo(), d.getNome()))
				.toList();
        return ResponseEntity.ok(dtoList);
    }
}

class DisciplinaSearchDTO {
	private String codigo;
	private String nome;
	public DisciplinaSearchDTO(String codigo, String nome){
		this.codigo = codigo;
		this.nome = nome;
	}

	public String getCodigo() { return codigo; }
	public String getNome() { return nome; }

	public void setCodigo(String codigo) { this.codigo = codigo; }
	public void setNome(String nome) { this.nome = nome; }
}