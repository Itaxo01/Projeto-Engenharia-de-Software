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
    public ResponseEntity<List<Disciplina>> searchDisciplinas() {
        List<Disciplina> resultados = disciplinaService.buscarTodas();
        return ResponseEntity.ok(resultados);
    }
}
