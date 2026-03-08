package com.ecommerce.mulboutique.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiVersionRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path != null
                && path.startsWith("/api/")
                && !path.startsWith("/api/v1/")
                && !path.startsWith("/api-docs")
                && !path.startsWith("/swagger-ui")) {
            String target = "/api/v1" + path.substring(4);
            if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
                target = target + "?" + request.getQueryString();
            }
            response.setStatus(307);
            response.setHeader("Location", target);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
