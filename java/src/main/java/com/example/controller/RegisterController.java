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
import com.example.service.UserService.CreationResult;

import com.example.model.RegisterModel;
import org.springframework.web.bind.annotation.ModelAttribute;




@Controller
public class RegisterController {

	@Autowired
    private UserService userService;
	
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleRegister(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("pdf") MultipartFile pdf, Model model) {
		System.out.println("Register attempt: " + email);
		System.out.println("PDF received: " + (pdf != null ? pdf.getOriginalFilename() : "null"));
		System.out.println("Password received: " + (password != null ? "****" : "null"));
		
		// Se isso aqui der erro a gente não sabe o que aconteceu
		PdfValidationService.ValidationResult valid = PdfValidationService.validate(pdf);
		if (!valid.valid()) {
			model.addAttribute("error", valid.message());
			return "register";
		}

		CreationResult userCreation = userService.createUser(email, password);
		if(userCreation.success()) {
			return "redirect:/login";
		} else {
			model.addAttribute("error", userCreation.message());
			return "register";
		}
    }
}
