package com.example.model;

import java.util.ArrayList;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import com.example.model.ArquivoComentario;

/**
 * Entidade de domínio que representa um comentário feito por um usuário em uma avaliação.
 */

@Entity
@Table(name = "comentarios")
public class Comentario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_email")
	private User usuario;

	@Column(name = "up_votes")
	private Integer upVotes = 0;

	@Column(name = "down_votes")
	private Integer downVotes = 0;

	@Column(nullable = false, length = 2000)
	private String texto;

	@Column(name = "created_at")
	private Instant createdAt = Instant.now();

	// Relacionamento com avaliação
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avaliacao_id")
	private Avaliacao avaliacao;

	@OneToMany(mappedBy = "comentario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ArquivoComentario> arquivos = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pai_id")
	private Comentario pai;

	@OneToMany(mappedBy = "pai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Comentario> respostas = new ArrayList<>();

	public Comentario(){}

	/** Construtor completo utilizado pelo serviço/repositório. */
	public Comentario(User usuario, String texto) {
		this.usuario = usuario;
		this.texto = texto;
	}

	/** Construtor para comentário com pai */
	public Comentario(User usuario, String texto, Comentario pai) {
		this.usuario = usuario;
		this.texto = texto;
		this.pai = pai;
	}


	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public User getUsuario() { return usuario; }
	public void setUsuario(User usuario) { this.usuario = usuario; }

	public String getTexto() { return texto; }
	public void setTexto(String texto) { this.texto = texto; }

	public Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

	public List<ArquivoComentario> getArquivos() { return arquivos; }
	public void setArquivos(List<ArquivoComentario> arquivos) { this.arquivos = arquivos; }

	public Comentario getPai() { return pai; }
	public void setPai(Comentario pai) { this.pai = pai; }

	public List<Comentario> getRespostas() { return respostas; }
	public void setRespostas(List<Comentario> respostas) { this.respostas = respostas; }

	public Avaliacao getAvaliacao() { return avaliacao; }
	public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }

	public Integer getUpVotes() { return upVotes; }
	public void setUpVotes(Integer upVotes) { this.upVotes = upVotes; }

	public Integer getDownVotes() { return downVotes; }
	public void setDownVotes(Integer downVotes) { this.downVotes = downVotes; }

	// Métodos de conveniência
	public void addResposta(Comentario resposta) {
		respostas.add(resposta);
		resposta.setPai(this);
	}

	public void addArquivo(ArquivoComentario arquivo) {
		arquivos.add(arquivo);
		arquivo.setComentario(this);
	}

	public boolean isResposta() {
		return pai != null;
	}

	public boolean isComentarioPrincipal() {
		return pai == null;
	}

	public int getNivelProfundidade() {
		int nivel = 0;
		Comentario atual = this.pai;
		while (atual != null) {
			nivel++;
			atual = atual.getPai();
		}
		return nivel;
	}
}
