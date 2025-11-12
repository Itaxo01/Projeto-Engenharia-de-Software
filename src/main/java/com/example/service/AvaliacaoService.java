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

    @Autowired
    private UserService userService;

    @Autowired
    private ProfessorService professorService;

	 private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AvaliacaoService.class);
    // Criar nova avaliação
    public Avaliacao salvar(Avaliacao avaliacao) {
		  logger.debug("A");
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
		  logger.debug("Avaliação salva com ID: " + avaliacaoSalva.getId());

        // Marcar disciplina como avaliada no mapa curricular
        if (avaliacao.getDisciplina() != null && avaliacao.getUsuario() != null) {
            try {
                mapaCurricularService.marcarComoAvaliada(
                    avaliacao.getUsuario(), 
                    avaliacao.getDisciplina()
                );
            } catch (Exception e) {
                // Log do erro, mas não falha a operação principal
					 logger.error("Erro ao marcar disciplina como avaliada para usuário {}: {}", 
						  avaliacao.getUsuario().getUser_email(), e.getMessage());
                System.err.println("Erro ao marcar disciplina como avaliada: " + e.getMessage());
            }
        }
		  logger.debug("Avaliação salva com ID: " + avaliacaoSalva.getId());
        
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

	 // Create na verdade cria, edita e verifica se existe.
    public Optional<Avaliacao> create(Professor professor, Disciplina disciplina, Usuario usuario, Integer nota, Comentario comentario){
        Optional<Avaliacao> avaliacaoExistente;
		  logger.debug("Criando ou atualizando avaliação para usuário: " + usuario.getUser_email() +
			  		   ", disciplina: " + disciplina.getCodigo() +
			  		   (professor != null ? ", professor: " + professor.getProfessorId() : ", avaliação da disciplina"));

        if(disciplina == null || usuario == null){
            throw new IllegalArgumentException("Disciplina e usuario não podem ser nulos para criar avaliação.");
        }
        if(professor == null){
            avaliacaoExistente = avaliacaoRepository.findByProfessorIsNullAndDisciplinaAndUsuario(disciplina, usuario);
        }else{
            avaliacaoExistente = avaliacaoRepository.findByProfessorAndDisciplinaAndUsuario(professor, disciplina, usuario);
        }
		  
		  logger.debug("Avaliação existente: " + avaliacaoExistente.isPresent());
	
        if(avaliacaoExistente.isPresent()){
            Avaliacao avaliacao = avaliacaoExistente.get();
				
            if(comentario != null){
					logger.debug("Atualizando comentário da avaliação.");
                avaliacao.setComentario(comentario);
					logger.debug("Comentario atualizado.");

            }
            if(nota != -1){
                avaliacao.setNota(nota);
            }
				logger.debug("Salvando avaliação atualizada.");
            return Optional.of(salvar(avaliacao));
        } else {
				logger.debug("Avaliação não encontrada, criando nova avaliação.");
            Avaliacao avaliacao = new Avaliacao(nota, professor, disciplina, usuario, comentario);

				return Optional.of(salvar(avaliacao));
        }
    }

    public void addNota(String professorId, String disciplinaId, String usuarioEmail, Integer nota){
        Optional<Professor> p = professorService.buscarPorId(professorId);
        Optional<Disciplina> d = disciplinaService.buscarPorCodigo(disciplinaId);
        Usuario u = userService.getUser(usuarioEmail);
        if(p.isPresent() && d.isPresent() && u != null && nota >= 0 && nota <= 5){
            create(p.get(), d.get(), u, nota, null);
        } else {
            throw new IllegalArgumentException("Professor, Disciplina ou Usuário não encontrados.");
        }
    }

    // Verificar se existe avaliação
    public boolean existe(@NonNull Long id) {
        return avaliacaoRepository.existsById(id);
    }

    // Contar total de avaliações
    public long contar() {
        return avaliacaoRepository.count();
    }


	 /* O front-end vai usar isso aqui e processar para criar a página da disciplina */
	@Transactional(readOnly = true)
	public List<AvaliacaoDTO> buscarTodasAvaliacoesDisciplina(String disciplinaId, String sessionUsuarioEmail) {
		Optional<Disciplina> disciplinaOpt = disciplinaService.buscarPorCodigo(disciplinaId);

		if (disciplinaOpt.isEmpty()) {
			throw new IllegalArgumentException("Disciplina não existe.");
		}
		List<Avaliacao> avaliacoes = avaliacaoRepository.findAllAvaliacoesByDisciplina(disciplinaOpt.get());
		  
		return avaliacoes.stream()
			.map(avaliacao -> converterParaDTO(avaliacao, sessionUsuarioEmail))
			.collect(Collectors.toList());
	}

	private AvaliacaoDTO converterParaDTO(Avaliacao avaliacao, String sessionUsuarioEmail) {
        ComentarioDTO comentarioDTO = null;
        
        try {
				Comentario comentario = avaliacao.getComentario();
            if (avaliacao.hasComentario()) {
                comentarioDTO = new ComentarioDTO(
                    comentario.getComentarioId(),
                    comentario.getTexto(),
                    comentario.getUsuario() != null ? 
						  comentario.getUsuario().getNome() : "Usuário Anônimo",
                    comentario.getUpVotes(),
                    comentario.getDownVotes(),
                    comentario.getCreatedAt(),
						  comentario.getUsuario().getUser_email().equals(sessionUsuarioEmail),
						  comentario.hasVoted(sessionUsuarioEmail) 
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
            avaliacao.getNota() != null ? avaliacao.getNota() : 0,
            comentarioDTO,
            avaliacao.getCreatedAt()
        );
    }

	// DTO para Comentário
	public static class ComentarioDTO {
        private final Long id;
        private final String texto;
        private final Integer upVotes;
        private final Integer downVotes;
        private final Instant createdAt;
		  private final Boolean isOwner;
		  private final Integer hasVoted; // -1 = downvote, 0 = no vote, 1 = upvote

        
        public ComentarioDTO(Long id, String texto, String nomeUsuario, 
                           Integer upVotes, Integer downVotes, Instant createdAt, Boolean isOwner, Integer hasVoted) {
            this.id = id;
            this.texto = texto;
            this.upVotes = upVotes != null ? upVotes : 0;
            this.downVotes = downVotes != null ? downVotes : 0;
            this.createdAt = createdAt;
				this.isOwner = isOwner;
				this.hasVoted = hasVoted;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getTexto() { return texto; }
        public Integer getUpVotes() { return upVotes; }
        public Integer getDownVotes() { return downVotes; }
        public Instant getCreatedAt() { return createdAt; }
        public Boolean getIsOwner() { return isOwner; }
        public Integer getHasVoted() { return hasVoted; }
        
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
        private final Integer nota;
        private final ComentarioDTO comentario; // Null = sem comentário
        private final Instant createdAt;
        
        public AvaliacaoDTO(Long id, Disciplina disciplina, Professor professor, Integer nota, ComentarioDTO comentario, 
                           Instant createdAt) {
            this.id = id;
            this.disciplinaId = disciplina.getCodigo();
            this.professorId = professor != null ? professor.getProfessorId() : null;
            this.nota = nota;
            this.comentario = comentario;
            this.createdAt = createdAt;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getDisciplinaId() { return disciplinaId; }
        public String getProfessorId() { return professorId; }
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
