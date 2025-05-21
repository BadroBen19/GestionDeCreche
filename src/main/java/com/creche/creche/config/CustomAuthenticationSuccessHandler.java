package com.creche.creche.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    private final Map<String, String> roleTargetUrlMap;
    
    public CustomAuthenticationSuccessHandler() {
        roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin/dashboard");
        roleTargetUrlMap.put("ROLE_EDUCATOR", "/dashboard");
        roleTargetUrlMap.put("ROLE_KITCHEN", "/kitchen/dashboard");
        roleTargetUrlMap.put("ROLE_PARENT", "/parent/dashboard");
    }
    
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String targetUrl = "/dashboard"; // Default URL
        
        // Determine the URL based on user role
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (roleTargetUrlMap.containsKey(role)) {
                targetUrl = roleTargetUrlMap.get(role);
                break;
            }
        }
        
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}