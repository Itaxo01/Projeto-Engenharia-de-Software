package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.DTO.NotificacaoDTO;
import com.example.service.NotificacaoService;
import com.example.service.SessionService;
import com.example.service.UserService;

import com.example.model.Notificacao;
import com.example.model.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import okhttp3.Response;

@Controller
public class NotificationsController {

	@Autowired
	private NotificacaoService notificacaoService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationsController.class);

    @GetMapping("/notifications")
    public String notifications(Model model, HttpServletRequest request) {
		String userEmail = sessionService.getCurrentUser(request);
		logger.debug("Buscando usuário atual para listar notificações: " + userEmail);

		if (userEmail == null) {
			return "redirect:/login";
		}
		Usuario usuario = userService.getUser(userEmail);
		if (usuario == null) {
			return "redirect:/error";
		}
		logger.debug("Usuário encontrado: " + usuario.getUserEmail());

		List<NotificacaoDTO> notificacoes = notificacaoService.buscarNotificacoesPorEmail(usuario);

		logger.debug("Notificações encontradas: " + notificacoes.size());
		
		for (NotificacaoDTO notificacao : notificacoes) {
			logger.debug("Notificação: " + notificacao.texto() + " | Lida: " + notificacao.isRead());
		}

		model.addAttribute("notifications", notificacoes);

		return "notifications";
	}

	@PostMapping("/api/notifications/read")
	@ResponseBody
	public ResponseEntity<?> marcarNotificacao(@RequestParam("marcacao") Boolean marcacao,
												@RequestParam("notificacaoId") Long notificacaoId,
												HttpServletRequest request) {
		try {
			logger.info("Marcando notificacao");

			String usuarioEmail = sessionService.getCurrentUser(request);
			if (usuarioEmail == null) {
				return ResponseEntity.status(401).body("Usuário não autenticado.");
			}

			Usuario usuario = userService.getUser(usuarioEmail);
			Notificacao notificacao = notificacaoService.buscarPorId(notificacaoId);

			if(usuario == null || notificacao == null) {
				return ResponseEntity.badRequest().body("Usuário ou notificação inválidos.");
			}

			notificacaoService.marcarNotificacao(marcacao, usuario, notificacao);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.badRequest().build();
		}


	}
}




