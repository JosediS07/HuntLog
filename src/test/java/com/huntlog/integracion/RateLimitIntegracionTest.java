package com.huntlog.integracion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "huntlog.rate-limit.max-intentos=3",
        "huntlog.rate-limit.ventana-segundos=60"
})
@AutoConfigureMockMvc
class RateLimitIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void trasSuperarElLimite_loginDevuelve429() throws Exception {
        String jsonLogin = """
                {"email":"%s","password":"clave-incorrecta"}
                """.formatted("limite-" + UUID.randomUUID() + "@mail.com");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonLogin))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void otrasRutasNoSeVenAfectadasPorElLimite() throws Exception {
        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isUnauthorized());
    }
}
