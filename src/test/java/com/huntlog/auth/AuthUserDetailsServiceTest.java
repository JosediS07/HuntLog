package com.huntlog.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthUserDetailsService authUserDetailsService;

    @Test
    void loadUserByUsername_usuarioActivo_devuelveUserDetailsHabilitado() {
        User user = new User("Juan", "juan@mail.com", "encoded", "USER");
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("juan@mail.com");

        assertEquals("juan@mail.com", userDetails.getUsername());
        assertEquals("encoded", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_usuarioDesactivado_devuelveUserDetailsDeshabilitado() {
        User user = new User("Juan", "juan@mail.com", "encoded", "USER");
        user.setActivo(false);
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("juan@mail.com");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_emailNoExiste_lanzaExcepcion() {
        when(userRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authUserDetailsService.loadUserByUsername("noexiste@mail.com"));
    }
}
