package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DTO.NotificacaoDTO;
import com.example.model.Notificacao;
import com.example.model.Usuario;
import com.example.repository.NotificacaoRepository;

import jakarta.transaction.Transactional;

@Service
public class NotificacaoService {
	
	@Autowired
    private NotificacaoRepository notificacaoRepository;

	@Autowired UserService userService;


	@Transactional
	public List<NotificacaoDTO> buscarNotificacoesPorEmail(String userEmail) {
		List<Notificacao> notificacaos = notificacaoRepository.findByUsuarioUserEmailOrderByIdDesc(userEmail);

		return notificacaos.stream().map(n -> NotificacaoDTO.from(n)).toList();
	}

	@Transactional
	public long countUnreadNotifications(String userEmail) {
		List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioUserEmailOrderByIdDesc(userEmail);
		return notificacoes.stream().filter(n -> !n.getRead()).count();
	}

	@Transactional
	public void marcarNotificacao(Boolean marcacao, String userEmail, Long notificacaoId) {
		Usuario usuario = userService.getUser(userEmail);
		
		if (usuario == null) {
			throw new IllegalArgumentException("Usuário não existe.");
		}

		Optional<Notificacao> notificacaoOpt = notificacaoRepository.findById(notificacaoId); 
	
		if (notificacaoOpt.isEmpty()) {
			throw new IllegalArgumentException("Notificação não existe.");
		}

		Notificacao notificacao = notificacaoOpt.get();

		if (notificacao.getUsuario().getUserEmail() != userEmail) {
			throw new IllegalArgumentException("Usuário não é dono da notificação.");
		}

		notificacao.setRead(marcacao);
	}
}
