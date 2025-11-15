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

	 // Create na verdade cria ou edita avaliação (apenas nota)
    public Optional<Avaliacao> create(Professor professor, Disciplina disciplina, Usuario usuario, Integer nota){
        Optional<Avaliacao> avaliacaoExistente;
		  logger.debug("Criando ou atualizando avaliação para usuário: " + usuario.getUser_email() +
			  		   ", disciplina: " + disciplina.getCodigo() +
			  		   (professor != null ? ", professor: " + professor.getProfessorId() : ", avaliação da disciplina"));

        if(disciplina == null || usuario == null){
            throw new IllegalArgumentException("Disciplina e usuario não podem ser nulos para criar avaliação.");
        }
		  
		  if(nota == null || nota < 1 || nota > 5){
            throw new IllegalArgumentException("Nota deve estar entre 1 e 5.");
        }
		  
        if(professor == null){
            avaliacaoExistente = avaliacaoRepository.findByProfessorIsNullAndDisciplinaAndUsuario(disciplina, usuario);
        }else{
            avaliacaoExistente = avaliacaoRepository.findByProfessorAndDisciplinaAndUsuario(professor, disciplina, usuario);
        }
		  
		  logger.debug("Avaliação existente: " + avaliacaoExistente.isPresent());
	
        if(avaliacaoExistente.isPresent()){
            Avaliacao avaliacao = avaliacaoExistente.get();
            avaliacao.setNota(nota);
				logger.debug("Salvando avaliação atualizada.");
            return Optional.of(salvar(avaliacao));
        } else {
				logger.debug("Avaliação não encontrada, criando nova avaliação.");
            Avaliacao avaliacao = new Avaliacao(nota, professor, disciplina, usuario);
				return Optional.of(salvar(avaliacao));
        }
    }

    public void addNota(String professorId, String disciplinaId, String usuarioEmail, Integer nota){
        Optional<Professor> p = professorService.buscarPorId(professorId);
        Optional<Disciplina> d = disciplinaService.buscarPorCodigo(disciplinaId);
        Usuario u = userService.getUser(usuarioEmail);
        if(p.isPresent() && d.isPresent() && u != null && nota >= 1 && nota <= 5){
            create(p.get(), d.get(), u, nota);
        } else {
            throw new IllegalArgumentException("Professor, Disciplina ou Usuário não encontrados, ou nota inválida.");
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
        return new AvaliacaoDTO(
            avaliacao.getId(),
            avaliacao.getDisciplina(),
            avaliacao.getProfessor(),
            avaliacao.getNota() != null ? avaliacao.getNota() : 0,
            avaliacao.getCreatedAt()
        );
    }

	// DTO para Avaliação (apenas ratings, sem comentários)
	public static class AvaliacaoDTO {
        private final Long id;
        private final String disciplinaId;
        private final String professorId; // Null = avaliação da disciplina
        private final Integer nota;
        private final Instant createdAt;
        
        public AvaliacaoDTO(Long id, Disciplina disciplina, Professor professor, Integer nota, Instant createdAt) {
            this.id = id;
            this.disciplinaId = disciplina.getCodigo();
            this.professorId = professor != null ? professor.getProfessorId() : null;
            this.nota = nota;
            this.createdAt = createdAt;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getDisciplinaId() { return disciplinaId; }
        public String getProfessorId() { return professorId; }
        public Integer getNota() { return nota; }
        public Instant getCreatedAt() { return createdAt; }
        
        // Helper methods para o front-end
        public boolean isAvaliacaoDisciplina() { return professorId == null; }
        public boolean isAvaliacaoProfessor() { return professorId != null; }
        
        public String getDataFormatada() {
            return createdAt.atZone(ZoneId.systemDefault())
                           .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("pt-BR")));
        }
    }
}
