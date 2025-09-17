package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação Spring Boot principal do sistema acadêmico.
 */
@SpringBootApplication
public class AcademicSystemApplication {

    /**
     * Inicializa a aplicação Spring Boot.
     * @param args argumentos de linha de comando (Atualmente não são utilizados)
     */
    public static void main(String[] args) {
        SpringApplication.run(AcademicSystemApplication.class, args);
    }
}
