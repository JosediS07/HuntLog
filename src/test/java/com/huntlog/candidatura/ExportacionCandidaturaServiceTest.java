package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CandidaturaResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportacionCandidaturaServiceTest {

    private final ExportacionCandidaturaService exportacionService = new ExportacionCandidaturaService();

    @Test
    void generarCsv_incluyeEncabezadosYFilas() {
        CandidaturaResponse candidatura = new CandidaturaResponse(
                1L, 1L, "Acme, S.L.", "Ingeniero",
                "APPLIED", "https://oferta.com",
                BigDecimal.valueOf(30000), BigDecimal.valueOf(50000),
                "EUR", "Madrid", null,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 2, 11, 0),
                LocalDateTime.of(2025, 12, 31, 9, 0),
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        String csv = new String(exportacionService.generarCsv(List.of(candidatura)), StandardCharsets.UTF_8);

        assertTrue(csv.startsWith("Puesto,Empresa,Estado,Salario,Ubicación,URL Oferta,Aplicado,Respondido,Creado\r\n"));
        assertTrue(csv.contains("\"Acme, S.L.\""));
        assertTrue(csv.contains("Ingeniero"));
        assertTrue(csv.contains("30000 - 50000 EUR"));
    }

    @Test
    void generarCsv_sinDatos_incluyeSoloEncabezados() {
        String csv = new String(exportacionService.generarCsv(List.of()), StandardCharsets.UTF_8);

        assertTrue(csv.startsWith("Puesto,Empresa"));
        assertTrue(csv.endsWith("Creado\r\n"));
        assertEquals(1, csv.split("\r\n").length, "solo debe haber una línea de encabezados");
    }

    @Test
    void generarPdf_generaDocumentoValido() {
        CandidaturaResponse candidatura = new CandidaturaResponse(
                1L, 1L, "Acme", "Ingeniero",
                "APPLIED", null,
                null, null,
                "EUR", "Madrid", null,
                null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        byte[] pdf = exportacionService.generarPdf(List.of(candidatura));

        String cabecera = new String(pdf, 0, Math.min(5, pdf.length), StandardCharsets.ISO_8859_1);
        assertTrue(cabecera.equals("%PDF-"), "debe empezar con la cabecera %PDF-");
        assertTrue(pdf.length > 100);
    }
}
