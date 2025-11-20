package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.service.UserService;
import com.example.service.HashingService;

/**
 * Inicializa o banco de dados com dados padrão na primeira execução.
 * Cria um usuário administrador padrão se não existir.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nome}")
    private String adminNome;

    @Value("${admin.matricula}")
    private String adminMatricula;

    @Value("${admin.curso}")
    private String adminCurso;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o usuário administrador específico já existe
        if (userService.getUser(adminEmail) == null) {
            System.out.println("==============================================");
            System.out.println("🔧 Criando usuário administrador...");
            
            String hashedPassword = HashingService.hashPassword(adminPassword);
            userService.createUser(adminEmail, hashedPassword, adminNome, adminMatricula, adminCurso);
            
            System.out.println("✅ Usuário administrador criado com sucesso!");
            System.out.println("==============================================");

			} else {
				System.out.println("✅ Usuário administrador já existe: " + adminEmail);
			}
			if(!userService.getAdmin(adminEmail)) {
				System.out.println("🔧 Definindo usuário como administrador...");
				userService.toggleAdmin(adminEmail);
			} else {
				System.out.println("✅ Usuário já possui privilégios de administrador: " + adminEmail);
			}
    }
}
