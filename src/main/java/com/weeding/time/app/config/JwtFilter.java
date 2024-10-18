package com.weeding.time.app.config;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.service.JWTService;
import com.weeding.time.app.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String refreshHeader = request.getHeader("Refresh-Token");
        String token = null;
        String email = null;

        // Sprawdzanie access tokena
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);
        }

        // Walidacja access tokena
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(email);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Obsługa refresh tokena
        if (refreshHeader != null && refreshHeader.startsWith("Bearer ")) {
            String refreshToken = refreshHeader.substring(7);
            String newAccessToken;

            try {
                // Walidacja refresh tokena
                UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(jwtService.extractEmail(refreshToken));
                newAccessToken = jwtService.refreshAccessToken(refreshToken, userDetails); // Używaj obiektu UserDetails
                response.setHeader("Access-Token", newAccessToken); // Ustawienie nowego access tokena w nagłówku
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Ustawienie statusu 401 dla błędnego refresh tokena
                return; // Zatrzymanie dalszego przetwarzania
            }
        }

        filterChain.doFilter(request, response);
    }
}
