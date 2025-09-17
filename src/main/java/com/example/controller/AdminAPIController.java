package com.example.controller;

import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.model.User;
import com.example.service.SessionService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ArrayList;

/**
 * Controlador REST para fornecer os métodos para o usuário administrador.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAPIController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private SessionService sessionService;

	@GetMapping("/users")
	public ResponseEntity<ArrayList<UserDto>> getUsers(HttpServletRequest request) {
		boolean auth = sessionService.verifySession(request);
		if (!auth || !sessionService.currentUserIsAdmin(request)) {
			return ResponseEntity.status(403).build();
		}

		List<User> users = userService.getUsers();
		ArrayList<UserDto> usersRet = new ArrayList<UserDto>();
		users.forEach(user -> {
			usersRet.add(UserDto.from(user));
		});
		return ResponseEntity.ok(usersRet);

	}
	@PostMapping("/toggle-admin")
	public ResponseEntity<String> toggleAdmin(HttpServletRequest request, @RequestBody Map<String,String> body) {
		boolean auth = sessionService.verifySession(request);
		if (!auth || !sessionService.currentUserIsAdmin(request)) {
			return ResponseEntity.status(403).build();
		}
		String email = body.get("email");
		if(email != null) System.out.println("Toggle admin para: " + email);
		try {
			boolean success = userService.toggleAdmin(email);
			if(!success) {
				return ResponseEntity.status(404).body("Usuário não encontrado.");
			}
			return ResponseEntity.ok("Nível de administrador alterado com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao alterar nível de administrador.");
		}
	}

	@PostMapping("/delete-user")
	public ResponseEntity<String> deleteUser(HttpServletRequest request, @RequestBody Map<String,String> body) {
		boolean auth = sessionService.verifySession(request);
		if (!auth || !sessionService.currentUserIsAdmin(request)) {
			return ResponseEntity.status(403).build();
		}
		String email = body.get("email");
		if(email != null) System.out.println("Deletar conta: " + email);
		try {
			userService.deleteUser(email);
			return ResponseEntity.ok("Conta deletada com sucesso.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao deletar conta.");
		}
	}

	public record UserDto(String email, String nome, String matricula, String curso, boolean admin){
		/** Constrói o DTO a partir da entidade {@link com.example.model.User}. */
		public static UserDto from(User u){
			return new UserDto(u.getEmail(), u.getNome(), u.getMatricula(), u.getCurso(), u.getAdmin());
		}
	}
}
