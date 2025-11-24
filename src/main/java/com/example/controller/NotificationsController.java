package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.DTO.NotificacaoDTO;
import com.example.service.NotificacaoService;
import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import okhttp3.Response;

@Controller
public class NotificationsController {

	@Autowired
	private NotificacaoService notificacaoService;

    @Autowired
    private SessionService sessionService;

	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ComentarioController.class);

    @GetMapping("/notifications")
    public String notifications(Model model, HttpServletRequest request) {
        String userEmail = sessionService.getCurrentUser(request);
        if (userEmail == null) {
            return "redirect:/login";
        }

        List<NotificacaoDTO> notificacoes = notificacaoService.buscarNotificacoesPorEmail(userEmail);

		for (NotificacaoDTO notificacao : notificacoes) {
			logger.debug("Notificação: " + notificacao.texto() + " | Lida: " + notificacao.isRead());
		}

        model.addAttribute("notifications", notificacoes);
        model.addAttribute("isAdmin", sessionService.currentUserIsAdmin(request));
        model.addAttribute("unreadNotifications", notificacaoService.countUnreadNotifications(userEmail));

        return "notifications";
	}

	@PostMapping("/notifications/read")
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

			notificacaoService.marcarNotificacao(marcacao, usuarioEmail, notificacaoId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.badRequest().build();
		}


	}
}




