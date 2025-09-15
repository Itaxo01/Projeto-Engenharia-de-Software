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

/**
 * Controlador responsável pelo fluxo de registro de novos usuários.
 */
@Controller
public class RegisterController {

	@Autowired
    private UserService userService;
	
	/**
	 * Recebe o formulário de registro, valida o PDF e cria o usuário no repositório.
	 *
	 * @param email    email do usuário
	 * @param password senha em texto claro enviada pelo formulário
	 * @param pdf      comprovante em PDF para validação
	 * @param model    modelo para exibir mensagens de erro ao usuário
	 * @return redirect para "/login" em caso de sucesso ou view "register" com erros
	 */
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleRegister(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("pdf") MultipartFile pdf, Model model) {
		email = UserService.normalizeEmail(email);
		System.out.println("Tentativa de registro: " + email);
		System.out.println("PDF recebido: " + (pdf != null ? pdf.getOriginalFilename() : "null"));
		
		PdfValidationService.ValidationResult valid = PdfValidationService.validate(pdf);
		if (!valid.valid()) {
			System.out.println(valid.message());
			model.addAttribute("error", valid.message());
			return "register";
		}

		QueryResult userCreation = userService.createUser(email, password, valid.nome(), valid.matricula(), valid.curso());
		if(userCreation.success()) {
			System.out.println("Usuário registrado com sucesso: " + email);
			return "redirect:/login";
		} else {
			model.addAttribute("error", userCreation.message());
			return "register";
		}
    }
}
