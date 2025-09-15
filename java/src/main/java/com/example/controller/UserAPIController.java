package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserAPIController {
	/*
	 * Responsável por intermediar a consulta da web às informações do usuário, para posterior montagem da página DHTML.
	 * A requisição é feita no JavaScript dessa forma:
			const res = await fetch('/api/me', { credentials: 'same-origin' });
			// tratar respostas 401/404
        	const user = await res.json();
	 */
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/me")
	public ResponseEntity<UserDto> me(HttpServletRequest request) {
		String email = SessionService.getCurrentUser(request);
		if(email == null){
			return ResponseEntity.status(401).build();
		}
		User user = userRepository.getUser(email);
		if(user == null){
			return ResponseEntity.status(404).build();
		}
		return ResponseEntity.ok(UserDto.from(user));
	}

	public record UserDto(String email, String nome, String matricula, String curso){
		// carrega todas as informações exceto a senha.
		public static UserDto from(User u){
			return new UserDto(u.getEmail(), u.getNome(), u.getMatricula(), u.getCurso());
		}
	}

}
