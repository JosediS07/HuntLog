package com.huntlog.auth;

import com.huntlog.auth.dto.AuthResponse;
import com.huntlog.auth.dto.LoginRequest;
import com.huntlog.auth.dto.RefreshRequest;
import com.huntlog.auth.dto.RegisterRequest;
import com.huntlog.auth.dto.UsuarioResponse;
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

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refrescar(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refrescar(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(@Valid @RequestBody RefreshRequest request) {
        authService.cerrarSesion(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerPerfil(@AuthenticationPrincipal Long usuarioId) {
        User user = authService.obtenerUsuarioPorId(usuarioId);
        return ResponseEntity.ok(new UsuarioResponse(user.getId(), user.getNombre(), user.getEmail(), user.getRol()));
    }
}
