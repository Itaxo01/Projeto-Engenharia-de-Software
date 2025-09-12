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
		email = normalizeEmail(email);
		System.out.println("Login attempt: " + email);

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

	private String normalizeEmail(String email){
		if(email == null) return null;
		email = email.trim().toLowerCase();
		if(email.endsWith("@gmail.com")){
		  String[] parts = email.split("@");
        String localPart = parts[0];
        
        localPart = localPart.replace(".", "");
        
        if (localPart.contains("+")) {
            localPart = localPart.substring(0, localPart.indexOf("+"));
        }
        
        email = localPart + "@gmail.com";
		}
		return email;
	 }
	
}
