package com.example.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador responsável por capturar e tratar erros globais da aplicação e renderizar a página de erro.
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Manipula requisições para a rota "/error" populando informações úteis no modelo.
     *
     * @param request requisição HTTP de onde são extraídos atributos do erro
     * @param model   modelo utilizado para repassar dados à view (statusCode e errorMessage)
     * @return o nome da view de erro ("error")
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        
        return "error";
    }
}
