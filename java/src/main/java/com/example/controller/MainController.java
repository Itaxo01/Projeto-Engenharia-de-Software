package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import com.example.service.UserService;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
		boolean auth = UserService.verifySession(request);
		if (!auth) {
			return "redirect:/login";
		}
        return "dashboard";
    }

    @GetMapping("/user")
    public String userProfile(Model model) {
        // TODO: Add logic to fetch user data from database
        return "user";
    }

    @GetMapping("/class")
    public String classDetails(@RequestParam(required = false) String id, Model model) {
        // TODO: Add logic to fetch class details from database
        // For now, we'll add mock data for the template
        return "class";
    }

}
