package com.example.service;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionService {
	

	public static boolean verifySession(HttpServletRequest request) {
		HttpSession session =  request.getSession(false);
		if (session == null) return false;
		try {
			String email = session.getAttribute("email").toString();
			return email != null && !email.isEmpty();
		} catch(Exception e) {
			return false;
		}
	}

	public static void createSession(HttpServletRequest request, String email){
		HttpSession session = request.getSession();
		session.setAttribute("email", email);
		session.setMaxInactiveInterval(30*60);
	}
}
