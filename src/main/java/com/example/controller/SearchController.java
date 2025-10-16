package com.example.controller;

import com.example.model.Disciplina;
import com.example.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    @Autowired
    private DisciplinaService disciplinaService;
    
    @GetMapping("/class")
    public ResponseEntity<List<Disciplina>> searchDisciplinas(@RequestParam String query) {
        List<Disciplina> resultados = disciplinaService.searchByCodigoOrNome(query);
        return ResponseEntity.ok(resultados);
    }
}
