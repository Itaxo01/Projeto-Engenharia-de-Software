package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.model.Usuario;
import com.example.service.NotificacaoService;
import com.example.service.SessionService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Adiciona atributos globais ao modelo de todas as views.
 * Evita repetição de código nos controllers.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Adiciona informações do usuário logado em todas as páginas
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        String userEmail = sessionService.getCurrentUser(request);
        
        if (userEmail != null) {
            Usuario usuario = userService.getUser(userEmail);
            
            if (usuario != null) {
                // Adicionar informações do usuário
                model.addAttribute("userEmail", userEmail);
                model.addAttribute("isAdmin", usuario.getAdmin());
                model.addAttribute("unreadNotifications", 
                    notificacaoService.countUnreadNotifications(usuario));
                model.addAttribute("userName", usuario.getNome());
                model.addAttribute("userCurso", usuario.getCurso());
            }
        }
    }
}