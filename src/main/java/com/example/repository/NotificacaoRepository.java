package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Notificacao;

@Repository
public interface NotificacaoRepository  extends JpaRepository<Notificacao, Long>{
	// Find notifications for a user by email, newest first
	java.util.List<Notificacao> findByUsuarioUserEmailOrderByIdDesc(String user_email);
}
