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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.factory.ComentarioFactory;
import com.example.model.Comentario;
import com.example.model.Disciplina;
import com.example.model.Professor;
import com.example.model.Usuario;
import com.example.repository.ComentarioRepository;

@Service
public class ComentarioService {
    
    @Autowired
    private ComentarioRepository comentarioRepository;

	 @Transactional // Admin use
	 public void softDelete(Long id, String deletedBy) {
			Comentario c = comentarioRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado"));
			
			softRecursiveDelete(c, deletedBy);
			comentarioRepository.save(c);
	 }

	 // recursiva por conta do save, salvar o pai é suficiente para persistir a deleção do ramo
	 public void softRecursiveDelete(Comentario c, String deletedBy) {
			c.setDeleted(true);
			c.setDeletedAt(Instant.now());
			c.setDeletedBy(deletedBy);

			for( Comentario filho : c.getFilhos()) {
				if (!filho.getDeleted()) {
					softRecursiveDelete(filho, deletedBy);
				}
			}
	 }

	 /**
     * Scheduled job: Archive old soft-deleted records
     * Runs daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void archiveOldDeletions() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        
        List<Comentario> oldDeleted = comentarioRepository
            .findByDeletedTrueAndDeletedAtBefore(cutoff);
        
        for (Comentario c : oldDeleted) {
            comentarioRepository.delete(c);
        }
    }


    // ✅ Criar comentário principal (com disciplina e professor)
    public Comentario criarComentario(Usuario usuario, String texto, Disciplina disciplina, Professor professor) {
        Comentario comentario = ComentarioFactory.createComentario(usuario, texto, disciplina, professor);
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
	@Transactional(readOnly = true)
    public Comentario responderComentario(Usuario usuario, String texto, Long parentId) {
        Comentario parent = comentarioRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário pai não encontrado"));
		Comentario resposta = ComentarioFactory.createReply(usuario, texto, parent);
		comentarioRepository.save(resposta); 
		return resposta;
    }
	 
	 // ✅ Buscar comentários de uma disciplina (sem professor)
	 @Transactional(readOnly = true)
	 public List<ComentarioDTO> buscarComentariosDisciplina(Disciplina disciplina, String sessionUsuarioEmail) {
		  List<Comentario> comentarios = comentarioRepository.findByDisciplinaAndProfessorIsNullAndPaiIsNull(disciplina);
		  return comentarios.stream()
		 		.map(c -> converterParaDTO(c, sessionUsuarioEmail))
				.collect(Collectors.toList());
	 }
	 
	 // ✅ Buscar comentários de um professor
	 @Transactional(readOnly = true)
	 public List<ComentarioDTO> buscarComentariosProfessor(Disciplina disciplina, Professor professor, String sessionUsuarioEmail) {
		  List<Comentario> comentarios = comentarioRepository.findByDisciplinaAndProfessorAndPaiIsNull(disciplina, professor);
		  return comentarios.stream()
				.map(c -> converterParaDTO(c, sessionUsuarioEmail))
				.collect(Collectors.toList());
	 }
	 
	 private ComentarioDTO converterParaDTO(Comentario comentario, String sessionUsuarioEmail) {
		  // Convert arquivos to ArquivoDTO list
		  List<ArquivoDTO> arquivos = comentario.getArquivos().stream()
				.map(arquivo -> new ArquivoDTO(
					 arquivo.getId(),
					 arquivo.getNomeOriginal(),
					 arquivo.getTipoMime(),
					 arquivo.getTamanho()
				))
				.collect(Collectors.toList());
		  
		  return new ComentarioDTO(
				comentario.getComentarioId(),
				comentario.getTexto(),
				comentario.getUpVotes(),
				comentario.getDownVotes(),
				comentario.getCreatedAt(),
				comentario.getUsuario() != null && comentario.getUsuario().getUserEmail().equals(sessionUsuarioEmail),
				comentario.hasVoted(sessionUsuarioEmail),
				comentario.getIsEdited(),
				comentario.getEditedAt(),
				comentario.getProfessor() != null ? comentario.getProfessor().getProfessorId() : null,
				arquivos
		  );
	 }
    
    // Buscar comentário por ID
    public Optional<Comentario> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }
    
    // Buscar todos os comentários
    public List<Comentario> buscarTodos() {
        return comentarioRepository.findAll();
    }

	 public List<Comentario> buscarNaoDeletados() {
		  return comentarioRepository.findByDeletedFalse();
	 }

	 /* 
	  * Hard delete
	  * Consider soft delete first
	  */
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
	 
	 // DTO para Comentário
	 public static class ComentarioDTO {
		  private final Long id;
		  private final String texto;
		  private final Integer upVotes;
		  private final String professorId;
		  private final Integer downVotes;
		  private final Instant createdAt;
		  private final Boolean isOwner;
		  private final Integer hasVoted; // -1 = downvote, 0 = no vote, 1 = upvote
		  private final Boolean edited;
		  private final Instant editedAt;
		  private final List<ArquivoDTO> arquivos;
		  
		  public ComentarioDTO(Long id, String texto, 
								Integer upVotes, Integer downVotes, Instant createdAt, 
								Boolean isOwner, Integer hasVoted, Boolean edited, Instant editedAt, String professorId, List<ArquivoDTO> arquivos) {
				this.id = id;
				this.texto = texto;
				this.upVotes = upVotes != null ? upVotes : 0;
				this.downVotes = downVotes != null ? downVotes : 0;
				this.createdAt = createdAt;
				this.isOwner = isOwner;
				this.hasVoted = hasVoted;
				this.edited = edited;
				this.editedAt = editedAt;
				this.professorId = professorId;
				this.arquivos = arquivos != null ? arquivos : new ArrayList<>();
		  }
		  
		  // Getters
		  public Long getId() { return id; }
		  public String getTexto() { return texto; }
		  public Integer getUpVotes() { return upVotes; }
		  public Integer getDownVotes() { return downVotes; }
		  public Instant getCreatedAt() { return createdAt; }
		  public Boolean getIsOwner() { return isOwner; }
		  public Integer getHasVoted() { return hasVoted; }
		  public Boolean getEdited() { return edited; }
		  public Instant getEditedAt() { return editedAt; }
		  public String getProfessorId() { return professorId; }
		  public List<ArquivoDTO> getArquivos() { return arquivos; }
		  
		  public String getDataFormatada() {
				return createdAt.atZone(ZoneId.systemDefault())
						  .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("pt-BR")));
		  }
	 }
	 
	 // DTO para Arquivo
	 public static class ArquivoDTO {
		  private final Long id;
		  private final String nomeOriginal;
		  private final String tipoMime;
		  private final Long tamanho;
		  
		  public ArquivoDTO(Long id, String nomeOriginal, String tipoMime, Long tamanho) {
				this.id = id;
				this.nomeOriginal = nomeOriginal;
				this.tipoMime = tipoMime;
				this.tamanho = tamanho;
		  }
		  
		  // Getters
		  public Long getId() { return id; }
		  public String getNomeOriginal() { return nomeOriginal; }
		  public String getTipoMime() { return tipoMime; }
		  public Long getTamanho() { return tamanho; }
		  
		  public boolean isImage() {
				return tipoMime != null && tipoMime.startsWith("image/");
		  }
		  
		  public boolean isPdf() {
				return "application/pdf".equals(tipoMime);
		  }
	 }
}