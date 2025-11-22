package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Serviço utilitário para manipulação de sessão HTTP do usuário autenticado.
 */
@Service
public class SessionService {
	@Autowired
	private UserService userService;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SessionService.class);
	/**
	 * Cria (ou reutiliza) uma sessão e armazena o email do usuário.
	 * @param request requisição HTTP
	 * @param email   email normalizado do usuário
	 */
	public void createSession(HttpServletRequest request, String email, boolean isAdmin){
		HttpSession session = request.getSession();
		session.setAttribute("email", email);
		session.setMaxInactiveInterval(30*60); // 30 minutos
		logger.info("Sessão criada para: " + email);
	}
	
	/**
	 * Invalida a sessão atual, removendo todos os atributos.
	 */
	public void deleteSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			String email = (String) session.getAttribute("email");
			logger.info("Sessão destruída para: " + email);
			session.invalidate();
		}
	}
	
	/**
	 * Obtém o email do usuário logado a partir da sessão.
	 * @return email ou null se não autenticado
	 */
	public String getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (String) session.getAttribute("email");
		}
		return null;
	}

	/**
	 * Verifica se o usuario logado é administrador.
	 * @return true se o usuário for admin, senão não.
	 */
	public boolean currentUserIsAdmin(HttpServletRequest request) {
		return userService.getAdmin(getCurrentUser(request));
	}

	/**
	 * Verifica se existe sessão válida para o usuário atual.
	 */
	public boolean verifySession(HttpServletRequest request) {
		String session = getCurrentUser(request);
		if (session == null) return false;
		try {
			return (!session.isEmpty() && userService.getUser(session) != null);
		} catch(Exception e) {
			return false;
		}
	}
}
