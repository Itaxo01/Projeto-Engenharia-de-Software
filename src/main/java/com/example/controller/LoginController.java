package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.UsuarioService;
import com.example.service.SessionService;

import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador responsável pelo fluxo de autenticação (login) de usuários.
 */
@Controller
public class LoginController {

	@Autowired
	private UsuarioService userService;
	@Autowired
	private SessionService sessionService;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoginController.class);
	
	/**
	 * Processa o formulário de login, valida as credenciais e cria a sessão.
	 *
	 * @param request  requisição HTTP (usada para criar a sessão)
	 * @param email    email do usuário
	 * @param password senha em texto claro enviada pelo formulário
	 * @param model    modelo para exibir mensagens de erro em caso de falha
	 * @return redirect para "/index" se sucesso ou view "login" em caso de erro
	 */
	@PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleLogin(HttpServletRequest request,  @RequestParam("email") String email, @RequestParam("password") String password, Model model) {
		logger.info("Tentativa de login: " + email);

		boolean authenticated = userService.validateUser(email, password);
		
		logger.info("Autenticação " + (authenticated ? "sucedida" : "falhou") + " para " + email);
		if(authenticated) {
			sessionService.createSession(request, email, userService.getIsAdmin(email));
			return "redirect:/index";
		} else {
			model.addAttribute("error", "Email ou senha inválidos.");
			return "login";
		}
	}
}
