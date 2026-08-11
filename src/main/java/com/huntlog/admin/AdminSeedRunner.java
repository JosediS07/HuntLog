package com.huntlog.admin;

import com.huntlog.auth.User;
import com.huntlog.auth.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeedRunner implements CommandLineRunner {

    private static final String ROL_ADMIN = "ADMIN";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeedRunner(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${admin.email:}") String adminEmail,
                           @Value("${admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() && adminPassword.isBlank()) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN_EMAIL y ADMIN_PASSWORD deben definirse juntos para crear el usuario admin");
        }
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }
        userRepository.save(new User("Administrador", adminEmail, passwordEncoder.encode(adminPassword), ROL_ADMIN));
    }
}
