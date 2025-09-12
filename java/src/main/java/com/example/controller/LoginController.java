package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.UserService;
import com.example.service.SessionService;

import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleLogin(HttpServletRequest request,  @RequestParam("email") String email, @RequestParam("password") String password, Model model) {

		System.out.println("Login attempt: " + email);
		System.out.println("Password received: " + (password != null ? "****" : "null"));

		boolean authenticated = userService.validateUser(email, password);
		
		System.out.println("Authentication " + (authenticated ? "succeeded" : "failed") + " for " + email);
		if(authenticated) {
			SessionService.createSession(request, email);
			return "redirect:/dashboard";
		} else {
			model.addAttribute("error", "Invalid email or password.");
			return "login";
		}
	}


	
}
