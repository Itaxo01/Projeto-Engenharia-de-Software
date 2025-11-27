package com.example.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.factory.ComentarioFactory;
import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.Usuario;
import com.example.repository.ComentarioRepository;

import com.example.DTO.ComentarioDTO;	


@Service
public class ComentarioService {
    
    @Autowired
    private ComentarioRepository comentarioRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	 public void delete(Comentario comentario) {
		  comentarioRepository.delete(comentario);
	 }

    // ✅ Criar comentário principal (com disciplina e professor)
    public Comentario criarComentario(Usuario usuario, String texto, Disciplina disciplina, Professor professor) {
        Comentario comentario = ComentarioFactory.criarComentario(usuario, texto, disciplina, professor, null);
		return comentarioRepository.save(comentario);
    }


	 public Comentario edit(Comentario comentario, String novoTexto) {
		  comentario.edit(novoTexto);
		  return comentarioRepository.save(comentario);
	 }

	 public Comentario salvar(Comentario comentario) {
		  return comentarioRepository.save(comentario);
	 }
    
    // Responder comentário (herda disciplina/professor do pai)
	@Transactional
    public Comentario responderComentario(Usuario usuario, String texto, Long parentId) {
        Comentario parent = comentarioRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário pai não encontrado"));

		Comentario resposta = ComentarioFactory.criarComentario(usuario, texto, null, null, parent);
		Comentario saved = comentarioRepository.save(resposta); 
		
		return saved;
    }
	 
	 // ✅ Buscar comentários de uma disciplina (sem professor)
	 @Transactional(readOnly = true)
	 public List<ComentarioDTO> buscarComentariosDisciplina(Disciplina disciplina, String sessionUsuarioEmail) {
		  List<Comentario> comentarios = comentarioRepository.findByDisciplinaAndProfessorIsNullAndPaiIsNull(disciplina);
		  return comentarios.stream()
		 		.map(c -> ComentarioDTO.from(c, sessionUsuarioEmail))
				.collect(Collectors.toList());
	 }
	 
	 // ✅ Buscar comentários de um professor
	 @Transactional(readOnly = true)
	 public List<ComentarioDTO> buscarComentariosProfessor(Disciplina disciplina, Professor professor, String sessionUsuarioEmail) {
		  List<Comentario> comentarios = comentarioRepository.findByDisciplinaAndProfessorAndPaiIsNull(disciplina, professor);
		  return comentarios.stream()
				.map(c -> ComentarioDTO.from(c, sessionUsuarioEmail))
				.collect(Collectors.toList());
	 }
    
    // Buscar comentário por ID
    public Optional<Comentario> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }
    
    // Buscar todos os comentários
    public List<Comentario> buscarTodos() {
        return comentarioRepository.findAll();
    }

    public void deletar(Long id) {
        comentarioRepository.deleteById(id);
    }
    
    // Verificar se existe comentário
    public boolean existe(Long id) {
        return comentarioRepository.existsById(id);
    }

	 public void vote(String userEmail, Long comentarioId, Boolean isUpVote) throws Exception {
		  Comentario comentario = comentarioRepository.findById(comentarioId)
					 .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado"));
		  
		  comentario.addUserVote(userEmail, isUpVote);
		  
		  comentarioRepository.save(comentario);
	 }
	 
	 
}