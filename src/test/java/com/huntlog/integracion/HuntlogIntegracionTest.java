package com.huntlog.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class HuntlogIntegracionTest {

    private static final String REGISTRO = """
            {"nombre":"Usuario","email":"%s","password":"password123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextoDeSpringCargaCorrectamente() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void registroYLoginConCredencialesValidasDevuelvenToken() throws Exception {
        String email = emailUnico();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        String jsonLogin = """
                {"email":"%s","password":"password123"}
                """.formatted(email);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginConCredencialesInvalidasDevuelve401() throws Exception {
        String jsonLogin = """
                {"email":"%s","password":"clave-incorrecta"}
                """.formatted(emailUnico());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registroConEmailDuplicadoDevuelve409() throws Exception {
        String email = emailUnico();
        String json = REGISTRO.formatted(email);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void accesoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioNoPuedeCrearCandidaturaConEmpresaDeOtroUsuario() throws Exception {
        String tokenPrimero = registrarYObtenerToken(emailUnico());

        MvcResult resultadoEmpresa = mockMvc.perform(post("/api/empresas")
                        .header("Authorization", "Bearer " + tokenPrimero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Empresa del primer usuario"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        long empresaId = objectMapper.readTree(resultadoEmpresa.getResponse().getContentAsString())
                .get("id").asLong();

        String tokenSegundo = registrarYObtenerToken(emailUnico());
        String jsonCandidatura = """
                {"empresaId":%d,"puesto":"Desarrollador"}
                """.formatted(empresaId);

        mockMvc.perform(post("/api/candidaturas")
                        .header("Authorization", "Bearer " + tokenSegundo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCandidatura))
                .andExpect(status().isNotFound());
    }

    private String registrarYObtenerToken(String email) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("token").asText();
    }

    private String emailUnico() {
        return "usuario-" + UUID.randomUUID() + "@mail.com";
    }
}
