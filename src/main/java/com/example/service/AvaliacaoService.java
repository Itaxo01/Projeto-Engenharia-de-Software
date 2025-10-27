package com.example.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Avaliacao;
import com.example.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    // Criar nova avaliação
    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    // Buscar avaliação por ID
    public Optional<Avaliacao> buscarPorId(Long id) {
        return avaliacaoRepository.findById(id);
    }

    // Buscar todas as avaliações
    public List<Avaliacao> buscarTodas() {
        return avaliacaoRepository.findAll();
    }

    // Deletar avaliação
    public void deletar(Long id) {
        avaliacaoRepository.deleteById(id);
    }

    // Verificar se existe avaliação
    public boolean existe(Long id) {
        return avaliacaoRepository.existsById(id);
    }

    // Contar total de avaliações
    public long contar() {
        return avaliacaoRepository.count();
    }


	 /* O front-end vai usar isso aqui e processar para criar a página da disciplina */
	public List<AvaliacaoDTO> buscarTodasAvaliacoesDisciplina(String disciplinaId) {
		List<Avaliacao> avaliacoes = avaliacaoRepository.findAllAvaliacoesByDisciplina(disciplinaId);
        
		return avaliacoes.stream()
			.map(this::converterParaDTO)
			.collect(Collectors.toList());
	}

	private AvaliacaoDTO converterParaDTO(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
            avaliacao.getId(),
            avaliacao.getDisciplinaId(),
            avaliacao.getProfessorId(), // Pode ser null
            avaliacao.getUserEmail(),
            avaliacao.getNota(),
            avaliacao.hasComentario() ? avaliacao.getComentario().getTexto() : null,
            avaliacao.hasComentario() && avaliacao.getComentario().getUsuario() != null ? 
                avaliacao.getComentario().getUsuario().getNome() : "Usuário Anônimo",
            avaliacao.getCreatedAt()
        );
    }

	 public static class AvaliacaoDTO {
        private final Long id;
        private final String disciplinaId;
        private final String professorId; // Null = avaliação da disciplina
        private final String userEmail;
        private final Integer nota;
        private final String comentario; // Null = sem comentário
        private final String nomeUsuario;
        private final Instant createdAt;
        
        public AvaliacaoDTO(Long id, String disciplinaId, String professorId, 
                           String userEmail, Integer nota, String comentario, 
                           String nomeUsuario, Instant createdAt) {
            this.id = id;
            this.disciplinaId = disciplinaId;
            this.professorId = professorId;
            this.userEmail = userEmail;
            this.nota = nota;
            this.comentario = comentario;
            this.nomeUsuario = nomeUsuario;
            this.createdAt = createdAt;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getDisciplinaId() { return disciplinaId; }
        public String getProfessorId() { return professorId; }
        public String getUserEmail() { return userEmail; }
        public Integer getNota() { return nota; }
        public String getComentario() { return comentario; }
        public String getNomeUsuario() { return nomeUsuario; }
        public Instant getCreatedAt() { return createdAt; }
        
        // Helper methods para o front-end
        public boolean isAvaliacaoDisciplina() { return professorId == null; }
        public boolean isAvaliacaoProfessor() { return professorId != null; }
        public boolean hasComentario() { return comentario != null && !comentario.trim().isEmpty(); }
        
        public String getDataFormatada() {
            return createdAt.atZone(ZoneId.systemDefault())
                           .format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR")));
        }
    }
}
