package com.example.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Serviço utilitário para manipulação de sessão HTTP do usuário autenticado.
 */
public class SessionService {
	
	/**
	 * Cria (ou reutiliza) uma sessão e armazena o email do usuário.
	 * @param request requisição HTTP
	 * @param email   email normalizado do usuário
	 */
	public static void createSession(HttpServletRequest request, String email){
		HttpSession session = request.getSession();
		session.setAttribute("email", email);
		session.setMaxInactiveInterval(30*60); // 30 minutos
		System.out.println("Sessão criada para: " + email);
	}
	
	/**
	 * Invalida a sessão atual, removendo todos os atributos.
	 */
	public static void deleteSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			String email = (String) session.getAttribute("email");
			System.out.println("Sessão destruída para: " + email);
			session.invalidate();
		}
	}
	
	/**
	 * Obtém o email do usuário logado a partir da sessão.
	 * @return email ou null se não autenticado
	 */
	public static String getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (String) session.getAttribute("email");
		}
		return null;
	}

	/**
	 * Verifica se existe sessão válida para o usuário atual.
	 */
	public static boolean verifySession(HttpServletRequest request) {
		String session = getCurrentUser(request);
		if (session == null) return false;
		try {
			return !session.isEmpty();
		} catch(Exception e) {
			return false;
		}
	}
}
