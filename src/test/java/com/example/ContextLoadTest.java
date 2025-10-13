package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.repository.ComentarioRepository;
import com.example.repository.AvaliacaoRepository;
import com.example.service.ComentarioService;
import com.example.service.AvaliacaoService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ContextLoadTest {

    @Autowired
    private ComentarioRepository comentarioRepository;
    
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    
    @Autowired
    private ComentarioService comentarioService;
    
    @Autowired
    private AvaliacaoService avaliacaoService;

    @Test
    public void contextLoads() {
        assertThat(comentarioRepository).isNotNull();
        assertThat(avaliacaoRepository).isNotNull();
        assertThat(comentarioService).isNotNull();
        assertThat(avaliacaoService).isNotNull();
    }
}
