package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.service.SessionService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para fornecer dados do usuário autenticado para consumo via DHTML.
 */
@RestController
@RequestMapping("/api")
public class UserAPIController {
	@Autowired
	private UserService userService;
	@Autowired
	private SessionService sessionService;
	/**
	 * Recurso que retorna dados do usuário logado. Respostas possíveis:
	 * - 200 com JSON (email, nome, matricula, curso) se autenticado
	 * - 401 se não houver sessão
	 * - 404 se o usuário não for encontrado no repositório
	 */
	@GetMapping("/me")
	public ResponseEntity<UserDto> me(HttpServletRequest request) {
		String email = sessionService.getCurrentUser(request);
		if(email == null){
			return ResponseEntity.status(401).build();
		}
		User user = userService.getUser(email);
		if(user == null){
			return ResponseEntity.status(404).build();
		}
		
		return ResponseEntity.ok(UserDto.from(user));
	}

	/** Troca a senha do usuário logado.
	 */
	@PostMapping("/changePassword")
	public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody Map<String,String> body) {
		String email = sessionService.getCurrentUser(request);
		// System.out.println(body.get("currentPassword") + " "+body.get("newPassword"));
		if(email != null) System.out.println("Troca de senha para: " + email);
		try {
			userService.changePassword(email, body.get("currentPassword"), body.get("newPassword"));
		} catch(Exception e) {
			if (e.getMessage() == "400") 
				return ResponseEntity.status(400).build();
			else if (e.getMessage() == "401")
				return ResponseEntity.status(401).build();
			return ResponseEntity.status(500).build();
		}
		return ResponseEntity.ok("Sucesso");
	}
	/** Deleta a conta do usuário logado.
	*/
	@PostMapping("/deleteUser")
	public ResponseEntity<String> deleteUser(HttpServletRequest request, @RequestBody Map<String,String> body) {
		String email = sessionService.getCurrentUser(request);
		String currentPassword = body.get("currentPassword");
		if(email != null) System.out.println("Deleção de conta para: " + email);
		else return ResponseEntity.status(401).build();
		try {
			if(!userService.validateUser(email, currentPassword)) return ResponseEntity.status(406).build();
			
			System.out.println("Deletando usuário: " + email);
			UserService.QueryResult result = userService.deleteUser(email);
			if(!result.success()) return ResponseEntity.status(400).body(result.message());
		} catch(Exception e) {
			return ResponseEntity.status(500).build();
		}
		sessionService.deleteSession(request);
		return ResponseEntity.ok("Sucesso");
	}

	/** DTO exposto pelo endpoint /api/me. */
	public record UserDto(String email, String nome, String matricula, String curso){
		/** Constrói o DTO a partir da entidade {@link com.example.model.User}. */
		public static UserDto from(User u){
			return new UserDto(u.getEmail(), u.getNome(), u.getMatricula(), u.getCurso());
		}
	}
}
