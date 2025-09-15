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

/**
 * Controlador REST para fornecer dados do usuário autenticado para consumo via DHTML.
 */
@RestController
@RequestMapping("/api")
public class UserAPIController {
	/**
	 * Recurso que retorna dados do usuário logado. Respostas possíveis:
	 * - 200 com JSON (email, nome, matricula, curso) se autenticado
	 * - 401 se não houver sessão
	 * - 404 se o usuário não for encontrado no repositório
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

	/** DTO exposto pelo endpoint /api/me. */
	public record UserDto(String email, String nome, String matricula, String curso){
		/** Constrói o DTO a partir da entidade {@link com.example.model.User}. */
		public static UserDto from(User u){
			return new UserDto(u.getEmail(), u.getNome(), u.getMatricula(), u.getCurso());
		}
	}
}
