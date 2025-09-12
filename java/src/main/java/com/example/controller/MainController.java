package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
      boolean auth = SessionService.verifySession(request);
		if (!auth) {
			return "login";
		}
		return "dashboard";
    }

    @GetMapping("/register")
    public String register(HttpServletRequest request, Model model) {
		boolean auth = SessionService.verifySession(request);
		if (!auth) {
			return "register";
		}
		return "dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
		boolean auth = SessionService.verifySession(request);
		if (!auth) {
			return "login";
		}
        return "dashboard";
    }

	 
    @GetMapping("/user")
    public String userProfile(HttpServletRequest request, Model model) {
		 boolean auth = SessionService.verifySession(request);
		 if (!auth) {
			return "login";
		}
		return "user";
	}

	
    @GetMapping("/class/{id}")
    public String classDetails(HttpServletRequest request, @PathParam("id") String classId, Model model) {
        boolean auth = SessionService.verifySession(request);
		  if (!auth) {
			return "login";
		}
		model.addAttribute("classId", classId);
        return "class";
		}

    @GetMapping("/class")   
    public String classDetai(HttpServletRequest request, @RequestParam("id") String classId, Model model) {
      boolean auth = SessionService.verifySession(request);
		 if (!auth) {
			return "login";
		 }  
		model.addAttribute("classId", classId);
        return "class";
    }
	 
	 
}
