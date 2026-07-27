package com.huntlog.auth;

import com.huntlog.auth.dto.AuthResponse;
import com.huntlog.auth.dto.LoginRequest;
import com.huntlog.auth.dto.RegisterRequest;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User user = new User(
                request.nombre(),
                request.email(),
                passwordEncoder.encode(request.password()),
                "USER"
        );
        userRepository.save(user);

        String token = jwtService.generarToken(user);
        return new AuthResponse(user.getId(), token, user.getNombre(), user.getEmail(), user.getRol());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        String token = jwtService.generarToken(user);
        return new AuthResponse(user.getId(), token, user.getNombre(), user.getEmail(), user.getRol());
    }

    @Transactional(readOnly = true)
    public User obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario", id));
    }
}
