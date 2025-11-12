package com.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.ArquivoComentario;
import com.example.model.Comentario;
import com.example.repository.ArquivoComentarioRepository;

@Service
public class ArquivoComentarioService {

    @Autowired
    private ArquivoComentarioRepository arquivoComentarioRepository;
    
    // Diretório onde os arquivos serão salvos (configurável via application.properties)
    @Value("${app.upload.dir:uploads/comentarios}")
    private String uploadDir;

    /**
     * Salva um arquivo enviado via MultipartFile
     */
    public ArquivoComentario salvarArquivo(MultipartFile file, Comentario comentario) throws IOException {
        // Criar diretório se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Gerar nome único para o arquivo
        String nomeOriginal = file.getOriginalFilename();
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        String nomeUnico = UUID.randomUUID().toString() + extensao;
        
        // Salvar arquivo no sistema de arquivos
        Path caminhoCompleto = uploadPath.resolve(nomeUnico);
        Files.copy(file.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
        
        // Criar entidade ArquivoComentario
        ArquivoComentario arquivo = new ArquivoComentario(
            nomeOriginal,
            nomeUnico,
            file.getContentType(),
            file.getSize(),
            caminhoCompleto.toString(),
            comentario
        );
        
        // Salvar no banco de dados
        ArquivoComentario saved = arquivoComentarioRepository.save(arquivo);
        
        // Adicionar ao comentário
        comentario.addArquivo(saved);
        
        return saved;
    }

    public ArquivoComentario salvar(ArquivoComentario arquivoComentario) {
        return arquivoComentarioRepository.save(arquivoComentario);
    }

    public Optional<ArquivoComentario> buscarPorId(Long id) {
        return arquivoComentarioRepository.findById(id);
    }

    public List<ArquivoComentario> buscarTodos() {
        return arquivoComentarioRepository.findAll();
    }

    public void deletar(Long id) {
        Optional<ArquivoComentario> arquivoOpt = arquivoComentarioRepository.findById(id);
        if (arquivoOpt.isPresent()) {
            ArquivoComentario arquivo = arquivoOpt.get();
            
            // Deletar arquivo do sistema de arquivos
            try {
                Path path = Paths.get(arquivo.getCaminhoArquivo());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Erro ao deletar arquivo físico: " + e.getMessage());
            }
            
            // Deletar do banco de dados
            arquivoComentarioRepository.deleteById(id);
        }
    }
}