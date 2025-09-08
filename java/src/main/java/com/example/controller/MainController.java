package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String dashboard(Model model) {
        // TODO: Add logic to fetch user classes from database
        // For now, we'll add mock data for the template
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

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email, @RequestParam String password, Model model) {
        // TODO: Implement authentication logic
        // For now, redirect to dashboard
        return "redirect:/dashboard";
    }

    
}
