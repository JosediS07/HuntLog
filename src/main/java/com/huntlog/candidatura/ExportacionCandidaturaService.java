package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CandidaturaResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExportacionCandidaturaService {

    private static final String[] ENCABEZADOS = {
            "Puesto", "Empresa", "Estado", "Salario", "Ubicación",
            "URL Oferta", "Aplicado", "Respondido", "Creado"
    };

    private static final String SEPARADOR = ",";
    private static final String SALTO_LINEA = "\r\n";

    public byte[] generarCsv(List<CandidaturaResponse> candidaturas) {
        StringBuilder builder = new StringBuilder();
        builder.append(unirFila(List.of(ENCABEZADOS))).append(SALTO_LINEA);
        candidaturas.forEach(candidatura ->
                builder.append(unirFila(filaDeCandidatura(candidatura))).append(SALTO_LINEA));
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generarPdf(List<CandidaturaResponse> candidaturas) {
        try {
            Document documento = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            PdfWriter.getInstance(documento, salida);
            documento.open();
            documento.add(new Paragraph("Candidaturas"));
            documento.add(crearTabla(candidaturas));
            documento.close();
            return salida.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el PDF", ex);
        }
    }

    private PdfPTable crearTabla(List<CandidaturaResponse> candidaturas) {
        PdfPTable tabla = new PdfPTable(ENCABEZADOS.length);
        tabla.setWidthPercentage(100);
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        for (String encabezado : ENCABEZADOS) {
            tabla.addCell(new PdfPCell(new Phrase(encabezado, fuenteEncabezado)));
        }
        Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 8);
        candidaturas.forEach(candidatura ->
                filaDeCandidatura(candidatura).forEach(valor ->
                        tabla.addCell(new PdfPCell(new Phrase(valor, fuenteCuerpo)))));
        return tabla;
    }

    private String unirFila(List<String> valores) {
        return String.join(SEPARADOR, valores.stream().map(this::escapar).toList());
    }

    private String escapar(String valor) {
        String valorNulo = valor == null ? "" : valor;
        if (valorNulo.contains(SEPARADOR) || valorNulo.contains("\"") || valorNulo.contains("\n")) {
            return "\"" + valorNulo.replace("\"", "\"\"") + "\"";
        }
        return valorNulo;
    }

    private List<String> filaDeCandidatura(CandidaturaResponse candidatura) {
        return List.of(
                candidatura.puesto(),
                candidatura.empresaNombre() == null ? "" : candidatura.empresaNombre(),
                candidatura.estado(),
                formatearSalario(candidatura),
                candidatura.ubicacion() == null ? "" : candidatura.ubicacion(),
                candidatura.urlOferta() == null ? "" : candidatura.urlOferta(),
                candidatura.aplicadoEn() == null ? "" : candidatura.aplicadoEn().toString(),
                candidatura.respondidoEn() == null ? "" : candidatura.respondidoEn().toString(),
                candidatura.creado() == null ? "" : candidatura.creado().toString()
        );
    }

    private String formatearSalario(CandidaturaResponse candidatura) {
        if (candidatura.salarioMin() == null && candidatura.salarioMax() == null) {
            return "";
        }
        String rango = candidatura.salarioMin() == null ? "" : candidatura.salarioMin().toPlainString();
        if (candidatura.salarioMax() != null) {
            rango += (rango.isEmpty() ? "" : " - ") + candidatura.salarioMax().toPlainString();
        }
        return rango + " " + candidatura.moneda();
    }
}
