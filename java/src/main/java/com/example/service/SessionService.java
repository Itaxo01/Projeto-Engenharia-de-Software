package com.example.service;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionService {
	

	
	public static void createSession(HttpServletRequest request, String email){
		HttpSession session = request.getSession();
		session.setAttribute("email", email);
		session.setMaxInactiveInterval(30*60);
	}
	
	public static void deleteSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			String email = (String) session.getAttribute("email");
			System.out.println("Destroying session for: " + email);
			
			// This removes ALL session data and invalidates the session
			session.invalidate();
		}
	}
	
	public static String getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (String) session.getAttribute("email");
		}
		return null;
	}

	public static boolean verifySession(HttpServletRequest request) {
		String session = getCurrentUser(request);
		if (session == null) return false;
		try {
			return !session.isEmpty();
		} catch(Exception e) {
			return false;
		}
	}
}
