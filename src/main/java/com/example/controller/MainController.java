package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;

/**
 * Controlador das rotas de navegação principais (login, registro, dashboard, perfil e detalhes de turma).
 * Basicamente tudo que não possui um controller dedicado.
 * O Interceptor já verifica a autenticação para essas rotas.
 */
@Controller
public class MainController {

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
		if (!SessionService.verifySession(request)) return "login";
		return "redirect:/dashboard";
    }

    /**
     * Exibe a página de registro ou redireciona para o dashboard caso já autenticado.
     */
    @GetMapping("/register")
    public String register(HttpServletRequest request, Model model) {
		if (!SessionService.verifySession(request)) return "register";
		return "redirect:/dashboard";
    }

    /**
     * Exibe o dashboard se autenticado, caso contrário retorna à tela de login.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
      model.addAttribute("isAdmin", SessionService.currentUserIsAdmin(request));
      return "dashboard";
    }

    /**
     * Exibe o perfil do usuário autenticado.
     */
    @GetMapping("/user")
    public String userProfile(HttpServletRequest request, Model model) {
      model.addAttribute("isAdmin", SessionService.currentUserIsAdmin(request));
		return "user";
	}

    @GetMapping("/admin")
    public String adminPanel(HttpServletRequest request, Model model) {
      return "admin";
    }

    /**
     * Exibe detalhes da turma via path parameter.
     */
    @GetMapping("/class/{id}")
    public String classDetails(HttpServletRequest request, @PathParam("id") String classId, Model model) {
		  model.addAttribute("isAdmin", SessionService.currentUserIsAdmin(request));
		  model.addAttribute("classId", classId);
        return "class";
		}

    /**
     * Exibe detalhes da turma via query string (?id=...).
     */
    @GetMapping("/class")   
    public String classDetai(HttpServletRequest request, @RequestParam("id") String classId, Model model) {
        model.addAttribute("isAdmin", SessionService.currentUserIsAdmin(request));
		  model.addAttribute("classId", classId);
        return "class";
    }
}
