package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador das rotas de navegação principais (login, registro, dashboard, perfil e detalhes de turma).
 * Basicamente tudo que não possui um controller dedicado.
 * O Interceptor já verifica a autenticação para essas rotas.
 */
@Controller
public class MainController {

	@Autowired
	private SessionService sessionService;

    /**
     * Redireciona a raiz para a tela de login. A verificação do login será feita lá. 
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    /**
     * Exibe a página de login ou redireciona para o dashboard caso já autenticado.
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
		if (!sessionService.verifySession(request)) return "login";
		return "redirect:/dashboard";
    }

    /**
     * Exibe a página de registro ou redireciona para o dashboard caso já autenticado.
     */
    @GetMapping("/register")
    public String register(HttpServletRequest request, Model model) {
		if (!sessionService.verifySession(request)) return "register";
		return "redirect:/dashboard";
    }

    /**
     * Exibe o perfil do usuário autenticado.
     */
    @GetMapping("/user")
    public String userProfile(HttpServletRequest request, Model model) {
      model.addAttribute("isAdmin", sessionService.currentUserIsAdmin(request));
		  return "user";
	  }

    @GetMapping("/admin")
    public String adminPanel(HttpServletRequest request, Model model) {
      return "admin";
    }
}
