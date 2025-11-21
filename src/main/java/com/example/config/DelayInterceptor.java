package com.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para simular um atraso nas respostas das APIs, útil para testar estados de carregamento no frontend.
 * O atraso pode ser configurado via propriedades de aplicação.
 */
@Component
public class DelayInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DelayInterceptor.class);

    @Value("${dev.simulate-delay.enabled:false}")
    private boolean delayEnabled;

    @Value("${dev.simulate-delay.milliseconds:0}")
    private long delayMilliseconds;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        
        // Only apply delay to API endpoints
        String uri = request.getRequestURI();
        
        if (delayEnabled && uri.startsWith("/api/")) {
            logger.debug("🐌 Simulating delay of {}ms for: {}", delayMilliseconds, uri);
            
            try {
                Thread.sleep(delayMilliseconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Delay interrupted for: {}", uri);
            }
        }
        
        return true;
    }
}