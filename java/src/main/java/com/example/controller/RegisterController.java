package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.service.PdfValidationService;
import com.example.service.UserService;
import com.example.service.UserService.QueryResult;

import org.springframework.http.MediaType;





@Controller
public class RegisterController {

	@Autowired
    private UserService userService;
	
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleRegister(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("pdf") MultipartFile pdf, Model model) {
		email = normalizeEmail(email);
		System.out.println("Register attempt: " + email);
		System.out.println("PDF received: " + (pdf != null ? pdf.getOriginalFilename() : "null"));
		
		// Se isso aqui der erro a gente não sabe o que aconteceu
		PdfValidationService.ValidationResult valid = PdfValidationService.validate(pdf);
		if (!valid.valid()) {
			model.addAttribute("error", valid.message());
			return "register";
		}

		QueryResult userCreation = userService.createUser(email, password, valid.nome(), valid.matricula(), valid.curso());
		if(userCreation.success()) {
			System.out.println("User registered successfully: " + email);
			return "redirect:/login";
		} else {
			model.addAttribute("error", userCreation.message());
			return "register";
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
