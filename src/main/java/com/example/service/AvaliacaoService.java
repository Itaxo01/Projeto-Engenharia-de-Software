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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Avaliacao;
import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.Usuario;
import com.example.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private MapaCurricularService mapaCurricularService;

    @Autowired
    private DisciplinaService disciplinaService;

    // Criar nova avaliação
    public Avaliacao salvar(Avaliacao avaliacao) {
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        
        // Marcar disciplina como avaliada no mapa curricular
        if (avaliacao.getDisciplina() != null && avaliacao.getUsuario() != null) {
            try {
                mapaCurricularService.marcarComoAvaliada(
                    avaliacao.getUsuario(), 
                    avaliacao.getDisciplina()
                );
            } catch (Exception e) {
                // Log do erro, mas não falha a operação principal
                System.err.println("Erro ao marcar disciplina como avaliada: " + e.getMessage());
            }
        }
        
        return avaliacaoSalva;
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

    public Avaliacao create(Professor professor, Disciplina disciplina, Usuario usuario, Integer nota, Comentario comentario){
        Optional<Avaliacao> avaliacaoExistente;
        if(disciplina == null || usuario == null){
            throw new IllegalArgumentException("Disciplina e usuario não podem ser nulos para criar avaliação.");
        }
        if(professor == null){
            avaliacaoExistente = avaliacaoRepository.findByProfessorIsNullAndDisciplinaAndUsuario(disciplina, usuario);
        }else{
            avaliacaoExistente = avaliacaoRepository.findByProfessorAndDisciplinaAndUsuario(professor, disciplina, usuario);
        }
        if(avaliacaoExistente.isPresent()){
            Avaliacao avaliacao = avaliacaoExistente.get();
            if(comentario != null && avaliacao.getComentario() == null){
                avaliacao.setComentario(comentario);
            }
            if(nota != -1){
                avaliacao.setNota(nota);
            }
            return salvar(avaliacao);
        } else {
            Avaliacao avaliacao = new Avaliacao(nota, professor, disciplina, usuario, comentario);
            return salvar(avaliacao);
        }
    }

    public Avaliacao create(Disciplina disciplina, Usuario usuario, Integer nota){
        return create(null, disciplina, usuario, nota, null);
    }
    public Avaliacao create(Professor professor, Disciplina disciplina, Usuario usuario, Integer nota){
        return create(professor, disciplina, usuario, nota, null);
    }

    // Verificar se existe avaliação
    public boolean existe(@NonNull Long id) {
        return avaliacaoRepository.existsById(id);
    }

    public boolean existe(Professor professor, Disciplina disciplina, Usuario usuario){
        if (disciplina == null || usuario == null) {
            throw new IllegalArgumentException("Disciplina e usuario não podem ser nulos para verificação.");
        }
        if (professor == null) {
            return avaliacaoRepository.existsByProfessorIsNullAndDisciplinaAndUsuario(disciplina, usuario);
        } else {
            return avaliacaoRepository.existsByProfessorAndDisciplinaAndUsuario(professor, disciplina, usuario);
        }
    }

    // Contar total de avaliações
    public long contar() {
        return avaliacaoRepository.count();
    }


	 /* O front-end vai usar isso aqui e processar para criar a página da disciplina */
	@Transactional(readOnly = true)
	public List<AvaliacaoDTO> buscarTodasAvaliacoesDisciplina(String disciplinaId) {
        Optional<Disciplina> disciplinaOpt = disciplinaService.buscarPorCodigo(disciplinaId);

        if (disciplinaOpt.isEmpty()) {
            throw new IllegalArgumentException("Disciplina não existe.");
        }
		
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllAvaliacoesByDisciplina(disciplinaOpt.get());
        
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
            avaliacao.getDisciplina(),
            avaliacao.getProfessor(),
            avaliacao.getUsuario(),
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
        
        public AvaliacaoDTO(Long id, Disciplina disciplina, Professor professor, 
                           Usuario usuario, Integer nota, ComentarioDTO comentario, 
                           Instant createdAt) {
            this.id = id;
            this.disciplinaId = disciplina.getCodigo();
            this.professorId = professor.getProfessorId();
            this.userEmail = usuario.getUser_email();
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
