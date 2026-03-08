package com.ecommerce.mulboutique.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Value("${rate.limit.requests:100}")
    private int maxRequests;

    @Value("${rate.limit.window-seconds:60}")
    private int windowSeconds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        long now = Instant.now().getEpochSecond();

        Window window = windows.computeIfAbsent(ip, k -> new Window(now, 0));
        synchronized (window) {
            if (now - window.start >= windowSeconds) {
                window.start = now;
                window.count = 0;
            }
            window.count++;
            if (window.count > maxRequests) {
                response.setStatus(429);
                response.getWriter().write("Rate limit exceeded");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static class Window {
        long start;
        int count;

        Window(long start, int count) {
            this.start = start;
            this.count = count;
        }
    }
}
