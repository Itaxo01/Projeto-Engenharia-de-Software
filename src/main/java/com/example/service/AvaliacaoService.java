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
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
	public List<AvaliacaoDTO> buscarTodasAvaliacoesDisciplina(String disciplinaId) {
		List<Avaliacao> avaliacoes = avaliacaoRepository.findAllAvaliacoesByDisciplina(disciplinaId);
        
		return avaliacoes.stream()
			.map(this::converterParaDTO)
			.collect(Collectors.toList());
	}

	private AvaliacaoDTO converterParaDTO(Avaliacao avaliacao) {
        ComentarioDTO comentarioDTO = null;
        
        try {
            if (avaliacao.hasComentario()) {
                comentarioDTO = new ComentarioDTO(
                    avaliacao.getComentario().getComentarioId(),
                    avaliacao.getComentario().getTexto(),
                    avaliacao.getComentario().getUsuario() != null ? 
                        avaliacao.getComentario().getUsuario().getNome() : "Usuário Anônimo",
                    avaliacao.getComentario().getUpVotes(),
                    avaliacao.getComentario().getDownVotes(),
                    avaliacao.getComentario().getCreatedAt()
                );
            }
        } catch (Exception e) {
            // Se houver erro ao carregar comentário (lazy loading), ignora
            System.err.println("Erro ao carregar comentário: " + e.getMessage());
        }
        
        return new AvaliacaoDTO(
            avaliacao.getId(),
            avaliacao.getDisciplinaId(),
            avaliacao.getProfessorId(),
            avaliacao.getUserEmail(),
            avaliacao.getNota() != null ? avaliacao.getNota() : 0,
            comentarioDTO,
            avaliacao.getCreatedAt()
        );
    }

	// DTO para Comentário
	public static class ComentarioDTO {
        private final Long id;
        private final String texto;
        private final String nomeUsuario;
        private final Integer upVotes;
        private final Integer downVotes;
        private final Instant createdAt;
        
        public ComentarioDTO(Long id, String texto, String nomeUsuario, 
                           Integer upVotes, Integer downVotes, Instant createdAt) {
            this.id = id;
            this.texto = texto;
            this.nomeUsuario = nomeUsuario;
            this.upVotes = upVotes != null ? upVotes : 0;
            this.downVotes = downVotes != null ? downVotes : 0;
            this.createdAt = createdAt;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getTexto() { return texto; }
        public String getNomeUsuario() { return nomeUsuario; }
        public Integer getUpVotes() { return upVotes; }
        public Integer getDownVotes() { return downVotes; }
        public Instant getCreatedAt() { return createdAt; }
        
        public String getDataFormatada() {
            return createdAt.atZone(ZoneId.systemDefault())
                           .format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR")));
        }
    }

	// DTO para Avaliação
	public static class AvaliacaoDTO {
        private final Long id;
        private final String disciplinaId;
        private final String professorId; // Null = avaliação da disciplina
        private final String userEmail;
        private final Integer nota;
        private final ComentarioDTO comentario; // Null = sem comentário
        private final Instant createdAt;
        
        public AvaliacaoDTO(Long id, String disciplinaId, String professorId, 
                           String userEmail, Integer nota, ComentarioDTO comentario, 
                           Instant createdAt) {
            this.id = id;
            this.disciplinaId = disciplinaId;
            this.professorId = professorId;
            this.userEmail = userEmail;
            this.nota = nota;
            this.comentario = comentario;
            this.createdAt = createdAt;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getDisciplinaId() { return disciplinaId; }
        public String getProfessorId() { return professorId; }
        public String getUserEmail() { return userEmail; }
        public Integer getNota() { return nota; }
        public ComentarioDTO getComentario() { return comentario; }
        public Instant getCreatedAt() { return createdAt; }
        
        // Helper methods para o front-end
        public boolean isAvaliacaoDisciplina() { return professorId == null; }
        public boolean isAvaliacaoProfessor() { return professorId != null; }
        public boolean hasComentario() { return comentario != null; }
        
        public String getDataFormatada() {
            return createdAt.atZone(ZoneId.systemDefault())
                           .format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "BR")));
        }
    }
}
