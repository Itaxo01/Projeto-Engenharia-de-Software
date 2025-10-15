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
import com.example.scrapper.DisciplinaScrapper;
import com.example.service.ScrapperStatusService;
import com.example.service.SessionService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

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
	@Autowired
	private DisciplinaScrapper disciplinaScrapper;
	@Autowired
	private ScrapperStatusService scrapperStatusService;

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

	/**
	 * Endpoint para obter status do scrapper de disciplinas
	 */
	@GetMapping("/scrapper/status")
	public ResponseEntity<?> getScrapperStatus(HttpServletRequest request) {
		boolean auth = sessionService.verifySession(request);
		if (!auth || !sessionService.currentUserIsAdmin(request)) {
			return ResponseEntity.status(403).build();
		}
		
		// System.out.println(sessionService.getCurrentUser(request) + " solicitou status do scrapper");
		try {
			var status = scrapperStatusService.getUltimoStatus();
			return ResponseEntity.ok(status);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao obter status do scrapper: " + e.getMessage());
		}
	}

	/**
	 * Endpoint para executar scrapping de disciplinas
	 */
	@PostMapping("/scrapper/execute")
	public ResponseEntity<String> executeScrapper(HttpServletRequest request, @RequestBody Map<String,String> body) {
		boolean auth = sessionService.verifySession(request);
		if (!auth || !sessionService.currentUserIsAdmin(request)) {
			return ResponseEntity.status(403).build();
		}
		// System.out.println(sessionService.getCurrentUser(request) + " solicitou execução do scrapper");
		
		String cagrUsername = body.get("cagrUsername");
		String cagrPassword = body.get("cagrPassword");
		
		if (cagrUsername == null || cagrUsername.trim().isEmpty() || 
			cagrPassword == null || cagrPassword.trim().isEmpty()) {
			return ResponseEntity.status(400).body("Usuário e senha do CAGR são obrigatórios.");
		}
		
		try {
			// Obter nome do usuário atual para rastreamento
			String currentUserEmail = sessionService.getCurrentUser(request);
			String adminName = currentUserEmail != null ? currentUserEmail : "Administrador";
			
			// Executa em thread separada para não bloquear a requisição
			new Thread(() -> {
				try {
					disciplinaScrapper.executarScraping(cagrUsername.trim(), cagrPassword.trim(), adminName);
				} catch (Exception e) {
					System.err.println("Erro durante scraping executado por " + adminName + ": " + e.getMessage());
				}
			}).start();
			
			return ResponseEntity.ok("Scrapping iniciado com sucesso. Verifique o status para acompanhar o progresso.");
		} catch (IllegalStateException e) {
			return ResponseEntity.status(409).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao iniciar scrapping: " + e.getMessage());
		}
	}

	public record UserDto(String email, String nome, String matricula, String curso, boolean admin){
		/** Constrói o DTO a partir da entidade {@link com.example.model.User}. */
		public static UserDto from(User u){
			return new UserDto(u.getEmail(), u.getNome(), u.getMatricula(), u.getCurso(), u.getAdmin());
		}
	}
}
