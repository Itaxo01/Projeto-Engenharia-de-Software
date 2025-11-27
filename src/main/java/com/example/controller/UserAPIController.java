package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Usuario;
import com.example.service.SessionService;
import com.example.service.UsuarioService;

import com.example.DTO.UserDTO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para fornecer dados do usuário autenticado para consumo via DHTML.
 */
@RestController
@RequestMapping("/api")
public class UserAPIController {
	@Autowired
	private UsuarioService userService;
	@Autowired
	private SessionService sessionService;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserAPIController.class);
	/**
	 * Recurso que retorna dados do usuário logado. Respostas possíveis:
	 * - 200 com JSON (email, nome, matricula, curso) se autenticado
	 * - 401 se não houver sessão
	 * - 404 se o usuário não for encontrado no repositório
	 */
	@GetMapping("/me")
	public ResponseEntity<UserDTO> me(HttpServletRequest request) {
		String email = sessionService.getCurrentUser(request);
		if(email == null){
			return ResponseEntity.status(401).build();
		}
		Usuario user = userService.getUsuario(email);
		if(user == null){
			return ResponseEntity.status(404).build();
		}
		
		return ResponseEntity.ok(UserDTO.from(user));
	}

	/** Troca a senha do usuário logado.
	 */
	@PostMapping("/changePassword")
	public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody Map<String,String> body) {
		String email = sessionService.getCurrentUser(request);
		if(email == null) {
			return ResponseEntity.status(401).body("Usuário não autenticado");
		}

		Usuario user = userService.getUsuario(email);
		if(user == null) {
			return ResponseEntity.status(404).body("Usuario não encontrado");
		}
		String currentPassword = body.get("currentPassword");
		String newPassword = body.get("newPassword");

		if(!userService.validateUser(email, currentPassword)) {
			return ResponseEntity.status(406).body("Senha atual incorreta");
		}
		try { 
			user = userService.changePassword(email, currentPassword, newPassword);
			if(user == null) {
				return ResponseEntity.status(500).body("Erro ao alterar senha");
			}
			logger.debug("Senha alterada para o usuário: " + email);
			return ResponseEntity.ok("Sucesso");
		} catch(Exception e) {
			return ResponseEntity.status(500).body("Erro ao alterar senha");
		}
	}
	/** Deleta a conta do usuário logado.
	*/
	@PostMapping("/deleteUser")
	public ResponseEntity<?> deleteUser(HttpServletRequest request, @RequestBody Map<String,String> body) {
		String email = sessionService.getCurrentUser(request);
		String currentPassword = body.get("currentPassword");

		if(email != null) logger.info("Deleção de conta para: " + email);
		else return ResponseEntity.status(401).build();
		
		try {
			if(!userService.validateUser(email, currentPassword)) return ResponseEntity.status(406).build();
			
			logger.info("Deletando usuário: " + email);
			try{
				Usuario user = userService.getUsuario(email);
				if(user == null) return ResponseEntity.status(404).build();
				
				userService.delete(user);
				sessionService.deleteSession(request);

				return ResponseEntity.ok("Sucesso");
			
			} catch (Exception e) {
				return ResponseEntity.status(500).body("Erro ao deletar usuário");
			}
		} catch(Exception e) {
			return ResponseEntity.status(500).build();
		}
	}
}
