package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;

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


    @GetMapping("/class/{id}")
    public String classDetails(@PathParam("id") String classId, Model model) {
        model.addAttribute("classId", classId);
        return "class";
    }

    @GetMapping("/class")   
    public String classDetai(@RequestParam("id") String classId, Model model) {
        model.addAttribute("classId", classId);
        return "class";
    }

}
