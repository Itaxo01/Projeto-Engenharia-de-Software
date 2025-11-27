package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;



/**
 * Camada de serviço para regras de negócio relacionadas a usuários. A modificação do banco de dados é feita pelo repository, aqui há somente a validação e ponte entre o controller e o repository.
 */
@Service
public class UsuarioService {
    @Autowired
	 private UsuarioRepository usuarioRepository;

	 @Autowired
	 private ComentarioService comentarioService;

	 @Autowired
	 private AvaliacaoService avaliacaoService;

	 private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UsuarioService.class);

    
    /**
     * Cria um novo usuário após validar duplicidade de email e matrícula.
     * Senha é armazenada com hash BCrypt.
     */
    public Usuario create(String email, String password, String nome, String matricula, String curso) throws IllegalArgumentException {
		String hashPassword = HashingService.hashPassword(password);
		
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		if(usuarioOpt.isPresent()) {
			throw new IllegalArgumentException("Email já registrado.");
		} else {
			Usuario usuario = new Usuario(email, hashPassword, nome, matricula, curso);
			return usuarioRepository.save(usuario);
		}
    }

	 public Usuario create(Usuario usuario) throws IllegalArgumentException {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuario.getId());
		if(usuarioOpt.isPresent()) {
			throw new IllegalArgumentException("Email já registrado.");
		} else {
			return usuarioRepository.save(usuario);
		}
	 }		

	/**
	 * Deleta o usuário identificado pelo email.
	 * Um pouco mais complicado pois precisa deletar todas as relações do usuário com outras entidades.
	 */
	 public void delete(Usuario usuario){
		usuarioRepository.delete(usuario);
	 }
    
    /**
     * Valida login comparando a senha informada com o hash armazenado.
     */
	public boolean validateUser(String email, String password) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			String storedHash = usuario.getPassword();
			return HashingService.verifyPassword(password, storedHash);
		}

		logger.warn("Email não encontrado: " + email);
		return false;
	}

	public Usuario getUsuario(String email){
		return usuarioRepository.findByEmail(email).orElse(null);
	}

	public List<Usuario> getUsers() {
		return usuarioRepository.findAll();
	}
	public boolean toggleAdmin(String email){
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		if(usuarioOpt.isPresent()){
			Usuario usuario = usuarioOpt.get();
			usuario.toggleIsAdmin();
			usuarioRepository.save(usuario);
			return true;
		}
		return false;
	}

	public boolean getIsAdmin(String email){
		return usuarioRepository.getIsAdminByEmail(email);
	}


	/**
	 * Altera senha de usuário
	 */
	public Usuario changePassword (String email, String password, String newPassword) throws Exception {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		if(usuarioOpt.isPresent()) {
			Usuario user = usuarioOpt.get();
			if (!HashingService.verifyPassword(password, user.getPassword())) throw new Exception("400");
			
			user.setPassword(HashingService.hashPassword(newPassword));
			return usuarioRepository.save(user);
		} else {
			throw new Exception("401");
		}
	}
}