package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Listener.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.service.PdfValidationService;
import com.example.service.SessionService;
import com.example.service.UserService;
import com.example.service.UserService.QueryResult;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;

@Controller
public class LogoutController {

	@PostMapping(value = "/logout")
	public String handleLogout(HttpServletRequest request, Model model) {
		// Não requer mais verificações pois para fazer o acesso aqui é necessário ser autenticado
		boolean auth = SessionService.verifySession(request);
		if(auth){
			String account = SessionService.getCurrentUser(request);
			System.out.println("Logging out of the account " + account);
			SessionService.deleteSession(request);
		}
		// Meio estranho chegar num else aqui mas pode acontecer caso o token já tenha expirado no servidor e ainda esteja logado no browser
		// só vou redirecionar para o login
		return "redirect:/login";
	}
}
