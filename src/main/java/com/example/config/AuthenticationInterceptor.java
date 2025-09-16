package com.example.config;

import com.example.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    // URLs accessible to non-authenticated users
    private static final String[] PUBLIC_URLS = {
        "/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/error"
    };

    // URLs that require authentication but not admin
    private static final String[] USER_URLS = {
        "/dashboard", "/user", "/class", "/class/**", "/api/me", "/logout"
    };

    // URLs that require admin privileges
    private static final String[] ADMIN_URLS = {
        "/admin", "/admin/**", "/api/admin/**"
    };

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                           @NonNull Object handler) throws Exception {
        
        String requestURI = request.getRequestURI();
        boolean isAuthenticated = SessionService.verifySession(request);
        boolean isAdmin = isAuthenticated && SessionService.currentUserIsAdmin(request);

        // Check if URL is public
        if (isPublicUrl(requestURI)) {
            return true;
        }

        // Check if URL requires admin
        if (isAdminUrl(requestURI)) {
            if (!isAuthenticated) {
                response.sendRedirect("/login?error=notAuthenticated");
                return false;
            }
            if (!isAdmin) {
                response.sendRedirect("/dashboard?error=accessDenied");
                return false;
            }
            return true;
        }

        // Check if URL requires user authentication
        if (isUserUrl(requestURI)) {
            if (!isAuthenticated) {
                response.sendRedirect("/login?error=notAuthenticated");
                return false;
            }
            return true;
        }

        // Default: require admin authentication
		  if (!isAuthenticated) {
					response.sendRedirect("/login?error=notAuthenticated");
					return false;
			}
			if (!isAdmin) {
					response.sendRedirect("/dashboard?error=accessDenied");
					return false;
			}

        return true;
    }

    private boolean isPublicUrl(String uri) {
        return matchesPattern(uri, PUBLIC_URLS);
    }

    private boolean isUserUrl(String uri) {
        return matchesPattern(uri, USER_URLS);
    }

    private boolean isAdminUrl(String uri) {
        return matchesPattern(uri, ADMIN_URLS);
    }

    private boolean matchesPattern(String uri, String[] patterns) {
        for (String pattern : patterns) {
            if (pattern.endsWith("/**")) {
                String prefix = pattern.substring(0, pattern.length() - 3);
                if (uri.startsWith(prefix)) {
                    return true;
                }
            } else if (pattern.endsWith("**")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (uri.startsWith(prefix)) {
                    return true;
                }
            } else if (uri.equals(pattern)) {
                return true;
            }
        }
        return false;
    }
}