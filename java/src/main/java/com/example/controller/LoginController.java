package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.example.service.PdfValidationService;
import com.example.service.UserService;
import com.example.service.HashingService;
import com.example.service.UserService.CreationResult;

import com.example.model.RegisterModel;
import org.springframework.web.bind.annotation.ModelAttribute;
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
			HttpSession session = request.getSession();
			session.setAttribute("email", email);
			session.setMaxInactiveInterval(30*60);
			userService.createSession(session.getId(), email);
			return "redirect:/dashboard";
		} else {
			model.addAttribute("error", "Invalid email or password.");
			return "login";
		}
	}


	
}
