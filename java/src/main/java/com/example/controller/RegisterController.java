package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.service.PdfValidationService;
import com.example.service.UserService;
import com.example.service.UserService.CreationResult;

@Controller
public class RegisterController {

	@Autowired
    private UserService userService;
	
	
	 @PostMapping("/register")
    public String handleRegister(@RequestParam String email, @RequestParam String password, @RequestParam MultipartFile pdf, Model model) {
		// The verification logic was already held on the front end, so here we just create the user and redirect to login
		PdfValidationService.ValidationResult valid = PdfValidationService.validate(pdf);
		if (!valid.valid()) {
			model.addAttribute("error", valid.message());
			return "register";
		}
		// Create user logic here
		CreationResult userCreation = userService.createUser(email, password);
		if(userCreation.success()) {
			return "redirect:/login";
		} else {
			model.addAttribute("error", userCreation.message());
			return "register";
		}
    }
}
