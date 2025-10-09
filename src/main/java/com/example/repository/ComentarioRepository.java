package com.example.repository;

import com.example.model.Comentario;
import java.util.Optional;

public class ComentarioRepository {
	public Comentario save(Comentario comentario) {
		// Lógica para salvar o comentário no banco de dados
		return comentario;
	}

	public Optional<Comentario> findById(Long id) {
		// Lógica para buscar o comentário pelo ID no banco de dados
		return Optional.empty();
	}
}
