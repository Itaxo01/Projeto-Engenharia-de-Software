package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador responsável por realizar o logout do usuário autenticado.
 */
@Controller
public class LogoutController {

	/**
	 * Invalida a sessão atual (se existir) e redireciona para a tela de login.
	 *
	 * @param request requisição HTTP contendo a sessão
	 * @return redirect para "/login"
	 */
	@PostMapping(value = "/logout")
	public String handleLogout(HttpServletRequest request) {
		boolean auth = SessionService.verifySession(request);
		// pode gerar um erro no usuário não logado que tenta acessar a página, não sei ao certo o por que.
		if(auth){
			String account = SessionService.getCurrentUser(request);
			System.out.println("Saindo da conta " + account);
			SessionService.deleteSession(request);
		}
		return "redirect:/login";
	}
}
