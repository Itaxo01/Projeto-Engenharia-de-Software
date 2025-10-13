package com.example.model;

import jakarta.persistence.*;

/**
 * <p>Essa entidade é responsável por armazenar informações sobre arquivos que podem ser anexados a um comentário, como imagens, documentos, etc.</p>
 * <p>Atributos principais:</p>
 * <ul>
 *   <li>{@link #nomeOriginal} - Nome original do arquivo enviado pelo usuário.</li>
 *   <li>{@link #nomeArquivo} - Nome único do arquivo no sistema de arquivos.</li>
 *   <li>{@link #tipoMime} - Tipo MIME do arquivo (ex: "image/png", "application/pdf").</li>
 *   <li>{@link #tamanho} - Tamanho do arquivo em bytes.</li>
 *   <li>{@link #caminhoArquivo} - Caminho completo do arquivo no sistema de arquivos.</li>
 */
@Entity
@Table(name = "arquivos_comentario")
public class ArquivoComentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;
    
    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo; // Nome único do arquivo no sistema de arquivos
    
    @Column(name = "tipo_mime", length = 100)
    private String tipoMime;
    
    @Column(name = "tamanho")
    private Long tamanho; // Tamanho em bytes
    
    @Column(name = "caminho_arquivo", nullable = false, length = 500)
    private String caminhoArquivo; // Caminho completo do arquivo no sistema
    
    @Column(name = "created_at")
    private java.time.Instant createdAt = java.time.Instant.now();
    
    // Relacionamento com comentário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_id", nullable = false)
    private Comentario comentario;
    
    // Constructors
    public ArquivoComentario() {}
    
    public ArquivoComentario(String nomeOriginal, String nomeArquivo, String tipoMime, 
                           Long tamanho, String caminhoArquivo, Comentario comentario) {
        this.nomeOriginal = nomeOriginal;
        this.nomeArquivo = nomeArquivo;
        this.tipoMime = tipoMime;
        this.tamanho = tamanho;
        this.caminhoArquivo = caminhoArquivo;
        this.comentario = comentario;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNomeOriginal() { return nomeOriginal; }
    public void setNomeOriginal(String nomeOriginal) { this.nomeOriginal = nomeOriginal; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }

    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    public java.time.Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }

    public Comentario getComentario() { return comentario; }
    public void setComentario(Comentario comentario) { this.comentario = comentario; }

    @Override
    public String toString() {
        return "ArquivoComentario{" +
                "id=" + id +
                ", nomeOriginal='" + nomeOriginal + '\'' +
                ", nomeArquivo='" + nomeArquivo + '\'' +
                ", tipoMime='" + tipoMime + '\'' +
                ", tamanho=" + tamanho +
                '}';
    }
}
