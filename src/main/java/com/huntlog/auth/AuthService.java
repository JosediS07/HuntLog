package com.huntlog.auth;

import com.huntlog.auth.dto.AuthResponse;
import com.huntlog.auth.dto.LoginRequest;
import com.huntlog.auth.dto.RefreshRequest;
import com.huntlog.auth.dto.RegisterRequest;
import com.huntlog.auth.exception.CredencialesInvalidasException;
import com.huntlog.auth.exception.EmailYaRegistradoException;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException();
        }

        User user = new User(
                request.nombre(),
                request.email(),
                passwordEncoder.encode(request.password()),
                "USER"
        );
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailYaRegistradoException();
        }

        return construirAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        return construirAuthResponse(user);
    }

    @Transactional
    public AuthResponse refrescar(RefreshRequest request) {
        RefreshTokenService.RotacionResult resultado = refreshTokenService.rotar(request.refreshToken());
        return construirAuthResponse(resultado.usuario(), resultado.token());
    }

    @Transactional
    public void cerrarSesion(RefreshRequest request) {
        refreshTokenService.revocar(request.refreshToken());
    }

    @Transactional(readOnly = true)
    public User obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario", id));
    }

    private AuthResponse construirAuthResponse(User user) {
        return construirAuthResponse(user, refreshTokenService.generar(user));
    }

    private AuthResponse construirAuthResponse(User user, String refreshToken) {
        String accessToken = jwtService.generarToken(user);
        return new AuthResponse(user.getId(), user.getNombre(), user.getEmail(), user.getRol(),
                accessToken, refreshToken, jwtService.expiraEn(accessToken));
    }
}
