package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.UserService;
import com.example.service.SessionService;

import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador responsável pelo fluxo de autenticação (login) de usuários.
 */
@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	@Autowired
	private SessionService sessionService;
	
	/**
	 * Processa o formulário de login, valida as credenciais e cria a sessão.
	 *
	 * @param request  requisição HTTP (usada para criar a sessão)
	 * @param email    email do usuário
	 * @param password senha em texto claro enviada pelo formulário
	 * @param model    modelo para exibir mensagens de erro em caso de falha
	 * @return redirect para "/dashboard" se sucesso ou view "login" em caso de erro
	 */
	@PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleLogin(HttpServletRequest request,  @RequestParam("email") String email, @RequestParam("password") String password, Model model) {
		email = UserService.normalizeEmail(email);
		System.out.println("Tentativa de login: " + email);

		boolean authenticated = userService.validateUser(email, password);
		
		System.out.println("Autenticação " + (authenticated ? "sucedida" : "falhou") + " para " + email);
		if(authenticated) {
			sessionService.createSession(request, email, userService.getAdmin(email));
			return "redirect:/dashboard";
		} else {
			model.addAttribute("error", "Email ou senha inválidos.");
			return "login";
		}
	}
}
