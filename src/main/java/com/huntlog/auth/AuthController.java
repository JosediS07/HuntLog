package com.huntlog.auth;

import com.huntlog.auth.dto.AuthResponse;
import com.huntlog.auth.dto.LoginRequest;
import com.huntlog.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> obtenerPerfil(@AuthenticationPrincipal Long usuarioId) {
        User user = authService.obtenerUsuarioPorId(usuarioId);
        return ResponseEntity.ok(new AuthResponse(user.getId(), "", user.getNombre(), user.getEmail(), user.getRol()));
    }
}
