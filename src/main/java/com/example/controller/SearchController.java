package com.example.controller;

import com.example.model.Disciplina;
import com.example.model.ScrapperStatus;
import com.example.service.DisciplinaService;
import com.example.service.ScrapperStatusService;
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
    
    @Autowired
    private ScrapperStatusService scrapperStatusService;
    
    @GetMapping("/disciplinas")
    public ResponseEntity<List<DisciplinaSearchDTO>> searchDisciplinas() {
        List<Disciplina> resultados = disciplinaService.buscarTodas();
		  List<DisciplinaSearchDTO> dtoList = resultados.stream()
				.map(d -> DisciplinaSearchDTO.from(d))
				.toList();
        return ResponseEntity.ok(dtoList);
    }
    
    /**
     * Returns the expected count of disciplinas from last successful scrape
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getExpectedCount() {
        ScrapperStatus status = scrapperStatusService.getUltimoStatus();
        int count = status.getDisciplinasCapturadas();
        
        return ResponseEntity.ok(count);
    }
	
	private record DisciplinaSearchDTO (String codigo, String nome) {
		public static DisciplinaSearchDTO from(Disciplina u) {
			return new DisciplinaSearchDTO(u.getCodigo(), u.getNome());
		}
	}
}
