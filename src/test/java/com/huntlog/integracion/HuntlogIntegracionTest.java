package com.huntlog.integracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

        String jsonLogin = """
                {"email":"%s","password":"password123"}
                """.formatted(email);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
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

    @Test
    void listarConFiltroSalarialDevuelveSoloCandidaturasQueSeSolapan() throws Exception {
        String token = registrarYObtenerToken(emailUnico());
        long empresaId = crearEmpresa(token, "Empresa salarial");

        crearCandidatura(token, empresaId, "Junior", 25000, 35000);
        crearCandidatura(token, empresaId, "Senior", 45000, 65000);
        crearCandidatura(token, empresaId, "Sin salario", null, null);

        mockMvc.perform(get("/api/candidaturas")
                        .header("Authorization", "Bearer " + token)
                        .param("salarioDesde", "40000")
                        .param("salarioHasta", "60000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].puesto").value("Senior"));

        mockMvc.perform(get("/api/candidaturas")
                        .header("Authorization", "Bearer " + token)
                        .param("salarioDesde", "40000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].puesto").value("Senior"));

        mockMvc.perform(get("/api/candidaturas")
                        .header("Authorization", "Bearer " + token)
                        .param("salarioHasta", "35000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].puesto").value("Junior"));
    }

    @Test
    void listarConFiltroSalarialInvertidoDevuelve422() throws Exception {
        String token = registrarYObtenerToken(emailUnico());

        mockMvc.perform(get("/api/candidaturas")
                        .header("Authorization", "Bearer " + token)
                        .param("salarioDesde", "60000")
                        .param("salarioHasta", "40000"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void exportarCsvRespetaFiltrosDeSalario() throws Exception {
        String token = registrarYObtenerToken(emailUnico());
        long empresaId = crearEmpresa(token, "Empresa export");

        crearCandidatura(token, empresaId, "Junior export", 25000, 35000);
        crearCandidatura(token, empresaId, "Senior export", 45000, 65000);

        String csv = mockMvc.perform(get("/api/candidaturas/exportar")
                        .header("Authorization", "Bearer " + token)
                        .param("formato", "csv")
                        .param("salarioDesde", "40000"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        org.hamcrest.Matchers.containsString("candidaturas.csv")))
                .andReturn().getResponse().getContentAsString();

        assertThat(csv).contains("Senior export").doesNotContain("Junior export");
        assertThat(csv).startsWith("Puesto,Empresa");
    }

    @Test
    void exportarPdfDevuelveDocumento() throws Exception {
        String token = registrarYObtenerToken(emailUnico());
        long empresaId = crearEmpresa(token, "Empresa pdf");

        crearCandidatura(token, empresaId, "Candidatura pdf", null, null);

        byte[] pdf = mockMvc.perform(get("/api/candidaturas/exportar")
                        .header("Authorization", "Bearer " + token)
                        .param("formato", "pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        org.hamcrest.Matchers.containsString("candidaturas.pdf")))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();

        assertThat(new String(pdf, 0, 5, StandardCharsets.ISO_8859_1)).isEqualTo("%PDF-");
    }

    @Test
    void exportarConFormatoInvalidoDevuelve400() throws Exception {
        String token = registrarYObtenerToken(emailUnico());

        mockMvc.perform(get("/api/candidaturas/exportar")
                        .header("Authorization", "Bearer " + token)
                        .param("formato", "xlsx"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refrescarToken_devuelveParNuevoYRevocaElAnterior() throws Exception {
        String email = emailUnico();
        MvcResult registro = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = objectMapper.readTree(registro.getResponse().getContentAsString())
                .get("refreshToken").asText();

        MvcResult refrescado = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String refreshNuevo = objectMapper.readTree(refrescado.getResponse().getContentAsString())
                .get("refreshToken").asText();
        assertThat(refreshNuevo).isNotEqualTo(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevocaElRefreshTokenYSuReusoDevuelve401() throws Exception {
        String email = emailUnico();
        MvcResult registro = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = objectMapper.readTree(registro.getResponse().getContentAsString())
                .get("refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshConTokenInexistenteDevuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"token-que-no-existe"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private long crearEmpresa(String token, String nombre) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/empresas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"%s\"}".formatted(nombre)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
    }

    private void crearCandidatura(String token, long empresaId, String puesto,
                                  Integer salarioMin, Integer salarioMax) throws Exception {
        String json = """
                {"empresaId":%d,"puesto":"%s","salarioMin":%s,"salarioMax":%s}
                """.formatted(empresaId, puesto,
                salarioMin == null ? "null" : salarioMin,
                salarioMax == null ? "null" : salarioMax);

        mockMvc.perform(post("/api/candidaturas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    private String registrarYObtenerToken(String email) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRO.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private String emailUnico() {
        return "usuario-" + UUID.randomUUID() + "@mail.com";
    }
}
