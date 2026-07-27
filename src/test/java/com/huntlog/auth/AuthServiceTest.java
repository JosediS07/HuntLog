package com.huntlog.auth;

import com.huntlog.auth.dto.AuthResponse;
import com.huntlog.auth.dto.LoginRequest;
import com.huntlog.auth.dto.RegisterRequest;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_usuarioExitoso_devuelveToken() {
        RegisterRequest request = new RegisterRequest("Juan", "juan@mail.com", "password123");

        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtService.generarToken(any(User.class))).thenReturn("token_jwt");

        AuthResponse response = authService.registrar(request);

        assertNotNull(response.id());
        assertEquals("token_jwt", response.token());
        assertEquals("Juan", response.nombre());
        assertEquals("juan@mail.com", response.email());
        assertEquals("USER", response.rol());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registrar_emailDuplicado_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest("Juan", "juan@mail.com", "password123");
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registrar(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_credencialesValidas_devuelveToken() {
        LoginRequest request = new LoginRequest("juan@mail.com", "password123");

        User user = new User("Juan", "juan@mail.com", "encoded", "USER");
        user.setId(1L);

        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(jwtService.generarToken(user)).thenReturn("token_jwt");

        AuthResponse response = authService.login(request);

        assertEquals("token_jwt", response.token());
        assertEquals("Juan", response.nombre());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_emailNoExiste_lanzaExcepcion() {
        LoginRequest request = new LoginRequest("noexiste@mail.com", "password123");
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void obtenerUsuarioPorId_existe_devuelveUsuario() {
        User user = new User("Juan", "juan@mail.com", "encoded", "USER");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = authService.obtenerUsuarioPorId(1L);

        assertEquals("Juan", result.getNombre());
        assertEquals("juan@mail.com", result.getEmail());
    }

    @Test
    void obtenerUsuarioPorId_noExiste_lanzaExcepcion() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> authService.obtenerUsuarioPorId(999L));
    }
}
