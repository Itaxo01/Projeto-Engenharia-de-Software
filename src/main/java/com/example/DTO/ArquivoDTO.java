package com.example.DTO;

import com.example.model.ArquivoComentario;

public record ArquivoDTO(Long id, String nomeOriginal, String tipoMime, Long tamanho) {
	public static ArquivoDTO from(ArquivoComentario arquivo) {
		return new ArquivoDTO(
				arquivo.getId(),
				arquivo.getNomeOriginal(),
				arquivo.getTipoMime(),
				arquivo.getTamanho()
		);
	}

	public boolean isImage() {
		return tipoMime != null && tipoMime.startsWith("image/");
	}
	
	public boolean isPdf() {
		return "application/pdf".equals(tipoMime);
	}	
}