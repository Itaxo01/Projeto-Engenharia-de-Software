package com.example.model;

import java.time.Instant;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidade de domínio que representa um comentário feito por um usuário em uma avaliação.
 * <p>Um comentário pode ter arquivos anexados e pode ser uma resposta a outro comentário.</p>
 * <p>Um comentário pode ter várias respostas, formando uma árvore de comentários.</p>
 * <ul>
 * <li>{@link #usuario} Usuário que fez o comentário.</li>
 * <li>{@link #texto} Texto do comentário.</li>
 * <li>{@link #createdAt} Timestamp de quando o comentário foi criado.</li>
 * <li>{@link #avaliacao} Avaliação à qual o comentário principal está associado (pode ser nulo para respostas).</li>
 * <li>{@link #arquivos} Lista de arquivos anexados ao comentário.</li>
 * <li>{@link #pai} Comentário pai, se este comentário for uma resposta.</li>
 * <li>{@link #filhos} Lista de respostas a este comentário.</li>
 * <li>{@link #upVotes} Número de votos positivos no comentário.</li>
 * <li>{@link #downVotes} Número de votos negativos no comentário.</li>
 * </ul>
 */

@Entity
@Table(name = "comentarios", indexes = {
	@Index(name = "idx_pai_id", columnList = "pai_id"),
	@Index(name = "idx_user_created", columnList = "user_email, created_at"),
	@Index(name = "idx_created", columnList = "created_at")
})
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

	// Relacionamento com avaliação (comentário raiz)
	@OneToOne(mappedBy = "comentario", fetch = FetchType.LAZY)
	private Avaliacao avaliacao;

	@OneToMany(mappedBy = "comentario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private ArrayList<ArquivoComentario> arquivos = new ArrayList<>();

	// Relacionamento autoreferencial - comentário pai
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pai_id")
	private Comentario pai;

	// Relacionamento autoreferencial - comentários filhos
	@OneToMany(mappedBy = "pai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private ArrayList<Comentario> filhos = new ArrayList<>();

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

	public ArrayList<ArquivoComentario> getArquivos() { return arquivos; }
	public void setArquivos(ArrayList<ArquivoComentario> arquivos) { this.arquivos = arquivos; }

	public Comentario getPai() { return pai; }
	public void setPai(Comentario pai) { this.pai = pai; }

	public ArrayList<Comentario> getFilhos() { return filhos; }
	public void setFilhos(ArrayList<Comentario> filhos) { this.filhos = filhos; }

	public Avaliacao getAvaliacao() { return avaliacao; }
	public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }

	public Integer getUpVotes() { return upVotes; }
	public void setUpVotes(Integer upVotes) { this.upVotes = upVotes; }

	public Integer getDownVotes() { return downVotes; }
	public void setDownVotes(Integer downVotes) { this.downVotes = downVotes; }

	// Métodos de conveniência
	public void addFilho(Comentario filho) {
		filhos.add(filho);
		filho.setPai(this);
	}

	public void addArquivo(ArquivoComentario arquivo) {
		arquivos.add(arquivo);
		arquivo.setComentario(this);
	}

	public boolean isResposta() {
		return pai != null;
	}

	public boolean isComentarioPrincipal() {
		return pai == null && avaliacao != null;
	}

	public boolean isComentarioAninhado() {
		return pai != null;
	}

	public Comentario getComentarioRaiz() {
		Comentario atual = this;
		while (atual.getPai() != null) {
			atual = atual.getPai();
		}
		return atual;
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

	public int contarFilhosRecursivo() {
		int total = filhos.size();
		for (Comentario filho : filhos) {
			total += filho.contarFilhosRecursivo();
		}
		return total;
	}

	public boolean hasFilhos() {
		return filhos != null && !filhos.isEmpty();
	}

	public boolean hasArquivos() {
		return arquivos != null && !arquivos.isEmpty();
	}
}
