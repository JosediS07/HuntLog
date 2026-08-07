package com.huntlog.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitLoginFilter extends OncePerRequestFilter {

    private static final String RUTA_LOGIN = "/api/v1/auth/login";
    private static final String CUERPO_429 = """
            {"error":"TOO_MANY_REQUESTS","message":"Demasiados intentos de inicio de sesión. Inténtalo de nuevo más tarde.","status":429,"timestamp":"%s","detalles":[]}
            """;

    private final int maxIntentos;
    private final long ventanaMs;
    private final Map<String, Deque<Long>> intentosPorIp = new ConcurrentHashMap<>();

    public RateLimitLoginFilter(
            @Value("${huntlog.rate-limit.max-intentos:10}") int maxIntentos,
            @Value("${huntlog.rate-limit.ventana-segundos:60}") long ventanaSegundos) {
        this.maxIntentos = maxIntentos;
        this.ventanaMs = ventanaSegundos * 1000;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getMethod().equals("POST") || !request.getRequestURI().equals(RUTA_LOGIN);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr() != null ? request.getRemoteAddr() : "desconocido";
        long ahora = System.currentTimeMillis();

        synchronized (intentosPorIp) {
            Deque<Long> intentos = intentosPorIp.computeIfAbsent(ip, clave -> new ArrayDeque<>());
            long limite = ahora - ventanaMs;
            while (!intentos.isEmpty() && intentos.peekFirst() <= limite) {
                intentos.pollFirst();
            }
            if (intentos.size() >= maxIntentos) {
                escribirRespuesta429(response);
                return;
            }
            intentos.addLast(ahora);
        }

        filterChain.doFilter(request, response);
    }

    private void escribirRespuesta429(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(CUERPO_429.formatted(LocalDateTime.now()));
    }
}
