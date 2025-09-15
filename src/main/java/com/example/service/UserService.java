package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.repository.UserRepository;

/**
 * Camada de serviço para regras de negócio relacionadas a usuários. A modificação do banco de dados é feita pelo repository, aqui há somente a validação e ponte entre o controller e o repository.
 */
@Service
public class UserService {
    @Autowired
	 private UserRepository userRepository;

	/** Resultado padrão de operações de escrita. */
	public record QueryResult(boolean success, String message) {}
    
    /**
     * Cria um novo usuário após validar duplicidade de email e matrícula.
     * Senha é armazenada com hash BCrypt.
     */
    public QueryResult createUser(String email, String password, String nome, String matricula, String curso) {
        if (userRepository.emailExists(email)) {
            return new QueryResult(false, "Email já registrado.");
        }
		  if (userRepository.idExists(matricula)){
            return new QueryResult(false, "Matrícula já registrada.");
		  }
		  String hashPassword = HashingService.hashPassword(password);
		  userRepository.createUser(email, hashPassword, nome, matricula, curso);
        return new QueryResult(true, "Conta criada com sucesso");
    }

	/**
	 * Deleta o usuário identificado pelo email.
	 */
	 public QueryResult deleteUser(String email){
		if(!userRepository.emailExists(email)){
			return new QueryResult(false, "Essa conta não existe");
		}
		userRepository.deleteUser(email);
		return new QueryResult(true, "Conta deletada com sucesso!");
	 }
    
    /**
     * Valida login comparando a senha informada com o hash armazenado.
     */
    public boolean validateUser(String email, String password) {
		  if (!userRepository.emailExists(email)) {
				System.out.println("Email não encontrado: " + email);
				return false;
		  }
        String storedHash = userRepository.getPassword(email);
        return HashingService.verifyPassword(password, storedHash);
    }

	/**
	 * Normaliza emails (trim, lower-case, remove pontos/mais do Gmail).
	 */
	 public static String normalizeEmail(String email){
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

	/**
	 * Altera senha de usuário
	 */
	public boolean changePassword (String email, String password, String newPassword) throws Exception {
		if (!userRepository.emailExists(email)) throw new Exception("401");
		String userPassword = userRepository.getPassword(email);
		if (!HashingService.verifyPassword(password, userPassword)) throw new Exception("400");;
		userRepository.changePassword(email, HashingService.hashPassword(newPassword));
		return true;
	}
}