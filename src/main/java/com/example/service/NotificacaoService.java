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


	@Transactional
	public List<NotificacaoDTO> buscarNotificacoesPorEmail(Usuario usuario) {
		List<Notificacao> notificacaos = notificacaoRepository.findByUsuarioOrderByIdDesc(usuario);

		return notificacaos.stream().map(n -> NotificacaoDTO.from(n)).toList();
	}

	@Transactional
	public long countUnreadNotifications(Usuario usuario) {
		List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioOrderByIdDesc(usuario);
		return notificacoes.stream().filter(n -> !n.getRead()).count();
	}

	@Transactional
	public Notificacao buscarPorId(Long notificacaoId) {
		return notificacaoRepository.findById(notificacaoId).orElse(null);
	}

	@Transactional
	public void marcarNotificacao(Boolean marcacao, Usuario usuario, Notificacao notificacao) {
		
		if (usuario == null) {
			throw new IllegalArgumentException("Usuário não existe.");
		}

		if (notificacao == null) {
			throw new IllegalArgumentException("Notificação não existe.");
		}

		if (!notificacao.getUsuario().getUserEmail().equals(usuario.getUserEmail())) {
			throw new IllegalArgumentException("Usuário não é dono da notificação.");
		}

		notificacao.setRead(marcacao);
		notificacaoRepository.save(notificacao);
	}
}
