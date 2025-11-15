package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.ArquivoComentario;
import com.example.service.ArquivoComentarioService;

@Controller
@RequestMapping("/api/arquivos")
public class ArquivoController {
    
    @Autowired
    private ArquivoComentarioService arquivoService;
    
    // List of allowed MIME types for security
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        // Images
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "image/webp",
        // Documents
        "application/pdf",
        "application/msword", // .doc
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/vnd.ms-excel", // .xls
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
        "text/plain"
    );
    
    /**
     * Download or view a file
     * @param id File ID
     * @param download If true, force download; if false, display inline (for images/PDFs)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadArquivo(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean download) throws IOException {
        
        ArquivoComentario arquivo = arquivoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Arquivo não encontrado"));
        
        // Security check: validate MIME type
        if (!ALLOWED_MIME_TYPES.contains(arquivo.getTipoMime())) {
            return ResponseEntity.badRequest().build();
        }
        
        Path filePath = Paths.get(arquivo.getCaminhoArquivo());
        
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        
        byte[] data = Files.readAllBytes(filePath);
        ByteArrayResource resource = new ByteArrayResource(data);
        
        HttpHeaders headers = new HttpHeaders();
        
        // Set content type
        MediaType mediaType = MediaType.parseMediaType(arquivo.getTipoMime());
        headers.setContentType(mediaType);
        
        // If download=true or not an image/PDF, force download
        if (download || (!arquivo.getTipoMime().startsWith("image/") && !"application/pdf".equals(arquivo.getTipoMime()))) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + arquivo.getNomeOriginal() + "\"");
        } else {
            // Display inline (for images and PDFs)
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "inline; filename=\"" + arquivo.getNomeOriginal() + "\"");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .body(resource);
    }
}
