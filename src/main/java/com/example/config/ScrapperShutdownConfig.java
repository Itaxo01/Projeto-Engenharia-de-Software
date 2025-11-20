package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.example.service.ScrapperStatusService;

import jakarta.annotation.PreDestroy;

/**
 * Configuração para garantir que o status do scrapper seja consistente
 * mesmo em caso de shutdown inesperado do servidor.
 */
@Configuration
public class ScrapperShutdownConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScrapperShutdownConfig.class);

    @Autowired
    private ScrapperStatusService scrapperStatusService;

    /**
     * Verifica no startup se o scrapper estava executando e marca como interrompido.
     * Executa antes de outros CommandLineRunners (Order 0).
     */
    @Bean
    @Order(0)
    public CommandLineRunner checkScrapperStatusOnStartup() {
        return args -> {
            try {
                var status = scrapperStatusService.getUltimoStatus();
                
                if (status.isExecutando()) {
                    logger.warn("========================================");
                    logger.warn("⚠️  Scrapper estava executando quando o servidor foi interrompido!");
                    logger.warn("Última execução: {}", status.getUltimaExecucao());
                    logger.warn("Administrador: {}", status.getUltimoAdministrador());
                    logger.warn("Marcando como falha...");
                    logger.warn("========================================");
                    
                    // Marca como finalizado com erro
                    scrapperStatusService.marcarFimExecucao(
                        false, 
                        status.getDisciplinasCapturadas(), 
                        status.getProfessoresCapturados(), 
                        "Execução interrompida por shutdown do servidor"
                    );
                    
                    logger.info("✅ Status do scrapper corrigido. Pronto para nova execução.");
                }
            } catch (Exception e) {
                logger.error("Erro ao verificar status do scrapper no startup: {}", e.getMessage());
            }
        };
    }

    /**
     * Shutdown hook para garantir que o status seja atualizado
     * quando o servidor é desligado gracefully.
     */
    @PreDestroy
    public void onShutdown() {
        try {
            var status = scrapperStatusService.getUltimoStatus();
            
            if (status.isExecutando()) {
                logger.warn("========================================");
                logger.warn("🛑 Servidor sendo desligado durante execução do scrapper!");
                logger.warn("Salvando estado atual...");
                logger.warn("========================================");
                
                scrapperStatusService.marcarFimExecucao(
                    false,
                    status.getDisciplinasCapturadas(),
                    status.getProfessoresCapturados(),
                    "Execução interrompida por shutdown do servidor"
                );
                
                logger.info("✅ Status do scrapper salvo antes do shutdown.");
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar status do scrapper no shutdown: {}", e.getMessage());
        }
    }
}
