package com.huntlog.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huntlog.auth.User;
import com.huntlog.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void crearAdmin() {
        String email = emailUnico();
        User admin = new User("Administrador", email, passwordEncoder.encode("password123"), "ADMIN");
        userRepository.save(admin);
        emailAdmin = email;
    }

    private String emailAdmin;

    @Test
    void adminVeDashboardConMetricasGlobales() throws Exception {
        String token = loginYDevolverToken(emailAdmin, "password123");

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").isNumber())
                .andExpect(jsonPath("$.totalEmpresas").isNumber())
                .andExpect(jsonPath("$.totalCandidaturas").isNumber())
                .andExpect(jsonPath("$.candidaturasPorEstado").isMap())
                .andExpect(jsonPath("$.tasaRespuesta").isNumber());
    }

    @Test
    void usuarioConRolUserRecibe403EnDashboard() throws Exception {
        MvcResult resultadoRegistro = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Usuario","email":"%s","password":"password123"}
                                """.formatted(emailUnico())))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(resultadoRegistro.getResponse().getContentAsString())
                .get("accessToken").asText();

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void sinTokenRecibe401EnDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    private String loginYDevolverToken(String email, String password) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(resultado.getResponse().getContentAsString())
                .get("accessToken").asText();
        assertThat(token).isNotEmpty();
        return token;
    }

    private String emailUnico() {
        return "usuario-" + UUID.randomUUID() + "@mail.com";
    }
}
